package com.truckconnect.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shipments")
data class ShipmentEntity(
    @PrimaryKey val id: String,
    val origin: String,
    val destination: String,
    val goodsType: String,
    val weightTons: Double,
    val distanceKm: Double,
    val status: String,
    val reportingTime: String? = null,
    val pickupAddress: String? = null,
    val dropAddress: String? = null,
    val assignedDriverName: String? = null,
    val assignedVehicleReg: String? = null,
    val currentMilestone: String = "created",
    val isOfflineQueued: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
