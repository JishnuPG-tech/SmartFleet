package com.truckconnect.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proof_of_delivery")
data class ProofOfDeliveryEntity(
    @PrimaryKey val shipmentId: String,
    val localPhotoPaths: String, // comma-separated file paths
    val localSignaturePath: String? = null,
    val receiverName: String,
    val receiverOtp: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
