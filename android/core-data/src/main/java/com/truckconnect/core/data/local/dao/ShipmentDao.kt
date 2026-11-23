package com.truckconnect.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.truckconnect.core.data.local.entity.ShipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipmentDao {

    @Query("SELECT * FROM shipments ORDER BY updatedAt DESC")
    fun getAllShipments(): Flow<List<ShipmentEntity>>

    @Query("SELECT * FROM shipments WHERE id = :id LIMIT 1")
    fun getShipmentById(id: String): Flow<ShipmentEntity?>

    @Query("SELECT * FROM shipments WHERE status = :status ORDER BY updatedAt DESC")
    fun getShipmentsByStatus(status: String): Flow<List<ShipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipments(shipments: List<ShipmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: ShipmentEntity)

    @Update
    suspend fun updateShipment(shipment: ShipmentEntity)

    @Query("UPDATE shipments SET currentMilestone = :milestone, status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateMilestone(id: String, milestone: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM shipments")
    suspend fun clearShipments()
}
