package com.truckconnect.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.truckconnect.core.data.local.entity.LocationPingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPingDao {

    @Insert
    suspend fun insertPing(ping: LocationPingEntity): Long

    @Insert
    suspend fun insertPings(pings: List<LocationPingEntity>)

    @Query("SELECT * FROM location_pings WHERE isSynced = 0 ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getUnsyncedPings(limit: Int = 20): List<LocationPingEntity>

    @Query("UPDATE location_pings SET isSynced = 1 WHERE localId IN (:ids)")
    suspend fun markPingsSynced(ids: List<Long>)

    @Query("DELETE FROM location_pings WHERE isSynced = 1")
    suspend fun purgeSyncedPings()

    @Query("SELECT COUNT(*) FROM location_pings WHERE isSynced = 0")
    fun observeUnsyncedCount(): Flow<Int>
}
