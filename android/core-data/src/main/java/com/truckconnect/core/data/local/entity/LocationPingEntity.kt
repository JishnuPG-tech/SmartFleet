package com.truckconnect.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_pings")
data class LocationPingEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val lat: Double,
    val lng: Double,
    val speedKmh: Float,
    val bearing: Float,
    val accuracy: Float,
    val timestamp: Long,
    val shipmentId: String? = null,
    val isSynced: Boolean = false
)
