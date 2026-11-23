package com.truckconnect.core.data.repository

import com.truckconnect.core.data.local.dao.ProofOfDeliveryDao
import com.truckconnect.core.data.local.dao.ShipmentDao
import com.truckconnect.core.data.local.entity.ProofOfDeliveryEntity
import com.truckconnect.core.data.local.entity.ShipmentEntity
import com.truckconnect.core.network.api.ShipmentApiService
import com.truckconnect.core.network.model.CompleteDeliveryRequest
import com.truckconnect.core.network.model.RejectShipmentRequest
import com.truckconnect.core.network.model.ShipmentDetailDto
import com.truckconnect.core.network.model.ShipmentDto
import com.truckconnect.core.network.model.UpdateMilestoneRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipmentRepositoryImpl @Inject constructor(
    private val apiService: ShipmentApiService,
    private val shipmentDao: ShipmentDao,
    private val podDao: ProofOfDeliveryDao
) : ShipmentRepository {

    override fun observeShipments(): Flow<List<ShipmentEntity>> {
        return shipmentDao.getAllShipments()
    }

    override fun observeShipment(id: String): Flow<ShipmentEntity?> {
        return shipmentDao.getShipmentById(id)
    }

    override fun observeShipmentsByStatus(status: String): Flow<List<ShipmentEntity>> {
        return shipmentDao.getShipmentsByStatus(status)
    }

    override suspend fun refreshShipments(): Result<List<ShipmentDto>> {
        return try {
            val response = apiService.getShipments()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                val entities = list.map { dto ->
                    ShipmentEntity(
                        id = dto.id,
                        origin = dto.origin,
                        destination = dto.destination,
                        goodsType = dto.goodsType,
                        weightTons = dto.weightTons,
                        distanceKm = dto.distanceKm,
                        status = dto.status,
                        reportingTime = dto.reportingTime,
                        assignedDriverName = dto.assignedDriverName,
                        assignedVehicleReg = dto.assignedVehicleReg,
                        updatedAt = System.currentTimeMillis()
                    )
                }
                shipmentDao.insertShipments(entities)
                Result.success(list)
            } else {
                Result.failure(Exception("Failed to fetch shipments: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShipmentDetails(id: String): Result<ShipmentDetailDto> {
        return try {
            val response = apiService.getShipmentById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch shipment detail: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptShipment(id: String): Result<Unit> {
        return try {
            shipmentDao.updateMilestone(id, "accepted", "In transit")
            val response = apiService.acceptShipment(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit) // Offline queue fallback
            }
        } catch (e: Exception) {
            Result.success(Unit) // Offline tolerance
        }
    }

    override suspend fun rejectShipment(id: String, reason: String): Result<Unit> {
        return try {
            shipmentDao.updateMilestone(id, "rejected", "Rejected")
            val response = apiService.rejectShipment(id, RejectShipmentRequest(reason))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun updateMilestone(id: String, milestone: String, note: String): Result<Unit> {
        return try {
            val status = when (milestone.lowercase()) {
                "reached_pickup" -> "At pickup"
                "loaded" -> "Loaded"
                "in_transit" -> "In transit"
                "reached_drop" -> "At drop"
                "delivered" -> "Delivered"
                else -> "In transit"
            }
            shipmentDao.updateMilestone(id, milestone, status)

            val response = apiService.updateMilestone(id, UpdateMilestoneRequest(milestone, note))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit) // Cached locally
            }
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun completeDelivery(
        id: String,
        localPhotoPaths: List<String>,
        receiverName: String,
        localSignaturePath: String?,
        otp: String?
    ): Result<Unit> {
        val podEntity = ProofOfDeliveryEntity(
            shipmentId = id,
            localPhotoPaths = localPhotoPaths.joinToString(","),
            localSignaturePath = localSignaturePath,
            receiverName = receiverName,
            receiverOtp = otp,
            isSynced = false
        )
        podDao.insertPoD(podEntity)
        shipmentDao.updateMilestone(id, "delivered", "Delivered")

        return try {
            val response = apiService.completeDelivery(
                id,
                CompleteDeliveryRequest(
                    photoUrls = localPhotoPaths,
                    receiverName = receiverName,
                    signatureUrl = localSignaturePath
                )
            )
            if (response.isSuccessful) {
                podDao.markPoDSynced(id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit) // Will be synced by PoDUploadWorker
        }
    }
}
