package com.truckconnect.core.data.repository

import com.truckconnect.core.data.local.entity.ShipmentEntity
import com.truckconnect.core.network.model.ShipmentDetailDto
import com.truckconnect.core.network.model.ShipmentDto
import kotlinx.coroutines.flow.Flow

interface ShipmentRepository {
    fun observeShipments(): Flow<List<ShipmentEntity>>
    fun observeShipment(id: String): Flow<ShipmentEntity?>
    fun observeShipmentsByStatus(status: String): Flow<List<ShipmentEntity>>
    suspend fun refreshShipments(): Result<List<ShipmentDto>>
    suspend fun getShipmentDetails(id: String): Result<ShipmentDetailDto>
    suspend fun acceptShipment(id: String): Result<Unit>
    suspend fun rejectShipment(id: String, reason: String): Result<Unit>
    suspend fun updateMilestone(id: String, milestone: String, note: String = ""): Result<Unit>
    suspend fun completeDelivery(
        id: String,
        localPhotoPaths: List<String>,
        receiverName: String,
        localSignaturePath: String? = null,
        otp: String? = null
    ): Result<Unit>
}
