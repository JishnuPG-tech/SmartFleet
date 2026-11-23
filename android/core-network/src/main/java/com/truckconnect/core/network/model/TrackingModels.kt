package com.truckconnect.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationPingDto(
    val lat: Double,
    val lng: Double,
    val speedKmh: Float,
    val bearing: Float,
    val accuracy: Float,
    val timestamp: Long,
    val shipmentId: String? = null
)

@Serializable
data class BatchPingsRequest(
    val pings: List<LocationPingDto>
)

@Serializable
data class LiveVehicleDto(
    val id: String,
    val regNumber: String,
    val driverName: String,
    val shipmentId: String? = null,
    val status: String,
    val speedKmh: Float,
    val updatedAgoSeconds: Int,
    val eta: String? = null,
    val lat: Double,
    val lng: Double,
    val phone: String? = null
)
