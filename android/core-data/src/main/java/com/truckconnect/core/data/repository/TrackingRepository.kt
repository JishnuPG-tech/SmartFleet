package com.truckconnect.core.data.repository

import com.truckconnect.core.data.local.dao.LocationPingDao
import com.truckconnect.core.data.local.entity.LocationPingEntity
import com.truckconnect.core.network.api.TrackingApiService
import com.truckconnect.core.network.model.BatchPingsRequest
import com.truckconnect.core.network.model.LiveVehicleDto
import com.truckconnect.core.network.model.LocationPingDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TrackingRepository {
    suspend fun recordPing(
        lat: Double,
        lng: Double,
        speedKmh: Float,
        bearing: Float,
        accuracy: Float,
        shipmentId: String? = null
    ): Long

    suspend fun syncPendingPings(): Int
    suspend fun getLiveFleet(): Result<List<LiveVehicleDto>>
    fun observeUnsyncedCount(): Flow<Int>
}

@Singleton
class TrackingRepositoryImpl @Inject constructor(
    private val apiService: TrackingApiService,
    private val pingDao: LocationPingDao
) : TrackingRepository {

    override suspend fun recordPing(
        lat: Double,
        lng: Double,
        speedKmh: Float,
        bearing: Float,
        accuracy: Float,
        shipmentId: String?
    ): Long {
        val entity = LocationPingEntity(
            lat = lat,
            lng = lng,
            speedKmh = speedKmh,
            bearing = bearing,
            accuracy = accuracy,
            timestamp = System.currentTimeMillis(),
            shipmentId = shipmentId,
            isSynced = false
        )
        return pingDao.insertPing(entity)
    }

    override suspend fun syncPendingPings(): Int {
        val pending = pingDao.getUnsyncedPings(limit = 25)
        if (pending.isEmpty()) return 0

        val dtoList = pending.map {
            LocationPingDto(
                lat = it.lat,
                lng = it.lng,
                speedKmh = it.speedKmh,
                bearing = it.bearing,
                accuracy = it.accuracy,
                timestamp = it.timestamp,
                shipmentId = it.shipmentId
            )
        }

        return try {
            val response = apiService.ingestPings(BatchPingsRequest(dtoList))
            if (response.isSuccessful) {
                val ids = pending.map { it.localId }
                pingDao.markPingsSynced(ids)
                pending.size
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getLiveFleet(): Result<List<LiveVehicleDto>> {
        return try {
            val response = apiService.getLiveFleet()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch live fleet: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUnsyncedCount(): Flow<Int> {
        return pingDao.observeUnsyncedCount()
    }
}
