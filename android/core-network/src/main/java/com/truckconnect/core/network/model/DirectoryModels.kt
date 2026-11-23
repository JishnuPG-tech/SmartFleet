package com.truckconnect.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class DriverDto(
    val id: String,
    val name: String,
    val phone: String,
    val status: String, // available, on_trip, off_duty, pending_approval
    val rating: Double = 5.0,
    val tripsCount: Int = 0,
    val vehicleReg: String? = null,
    val avatarUrl: String? = null
)

@Serializable
data class DriverProfileDto(
    val id: String,
    val name: String,
    val phone: String,
    val status: String,
    val licenceNo: String,
    val licenceType: String,
    val licenceExpiry: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val emergencyContactRelation: String,
    val assignedVehicleReg: String? = null,
    val licenceFrontPhotoUrl: String? = null,
    val licenceBackPhotoUrl: String? = null
)

@Serializable
data class VehicleDto(
    val id: String,
    val regNumber: String,
    val model: String,
    val capacityTons: Double,
    val status: String,
    val assignedDriverName: String? = null,
    val currentLat: Double? = null,
    val currentLng: Double? = null
)

@Serializable
data class RejectDriverRequest(
    val reason: String
)
