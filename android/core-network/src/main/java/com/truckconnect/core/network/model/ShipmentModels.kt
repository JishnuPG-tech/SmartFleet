package com.truckconnect.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MilestoneDto(
    val milestone: String,
    val timestamp: String,
    val note: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

@Serializable
data class ShipmentDto(
    val id: String,
    val origin: String,
    val destination: String,
    val goodsType: String,
    val weightTons: Double,
    val distanceKm: Double,
    val status: String,
    val reportingTime: String? = null,
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val assignedVehicleId: String? = null,
    val assignedVehicleReg: String? = null,
    val createdAt: String? = null,
    val eta: String? = null
)

@Serializable
data class ShipmentDetailDto(
    val id: String,
    val origin: String,
    val destination: String,
    val goodsType: String,
    val weightTons: Double,
    val distanceKm: Double,
    val status: String,
    val reportingTime: String? = null,
    val pickupAddress: String? = null,
    val pickupContactName: String? = null,
    val pickupContactPhone: String? = null,
    val dropAddress: String? = null,
    val dropContactName: String? = null,
    val dropContactPhone: String? = null,
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val assignedVehicleId: String? = null,
    val assignedVehicleReg: String? = null,
    val milestones: List<MilestoneDto> = emptyList(),
    val podPhotoUrls: List<String> = emptyList(),
    val podSignatureUrl: String? = null,
    val receiverName: String? = null,
    val createdAt: String? = null
)

@Serializable
data class CreateShipmentRequest(
    val origin: String,
    val destination: String,
    val goodsType: String,
    val weightTons: Double,
    val distanceKm: Double,
    val reportingTime: String? = null,
    val pickupAddress: String? = null,
    val dropAddress: String? = null
)

@Serializable
data class AssignShipmentRequest(
    val driverId: String,
    val vehicleId: String
)

@Serializable
data class RejectShipmentRequest(
    val reason: String
)

@Serializable
data class UpdateMilestoneRequest(
    val milestone: String,
    val note: String = ""
)

@Serializable
data class CompleteDeliveryRequest(
    val photoUrls: List<String> = emptyList(),
    val receiverName: String,
    val signatureUrl: String? = null
)
