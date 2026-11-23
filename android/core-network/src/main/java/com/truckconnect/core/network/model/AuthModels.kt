package com.truckconnect.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminLoginRequest(
    val email: String,
    val password: String,
    val deviceModel: String? = null,
    val fcmToken: String? = null
)

@Serializable
data class DriverLoginRequest(
    val phone: String,
    val pin: String,
    val deviceModel: String? = null,
    val fcmToken: String? = null
)

@Serializable
data class DriverRegisterRequest(
    val name: String,
    val phone: String,
    val pin: String,
    val licenceNo: String,
    val licenceType: String,
    val licenceExpiry: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val emergencyContactRelation: String,
    val vehicleOwnership: String = "company_fleet",
    val vehicleRegNo: String? = null,
    val locationConsent: Boolean = true,
    val licenceFrontPhotoUrl: String? = null,
    val licenceBackPhotoUrl: String? = null
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    val role: String,
    val language: String? = "ml",
    val theme: String? = "indigo"
)

@Serializable
data class AuthResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class RegisterResponse(
    val message: String,
    val userId: String,
    val status: String
)

@Serializable
data class AppVersionResponse(
    val flavor: String,
    val versionCode: Int,
    val versionName: String,
    val minSupportedVersionCode: Int,
    val apkUrl: String,
    val sha256Checksum: String,
    val fileSizeBytes: Long,
    val hasUpdate: Boolean,
    val isForced: Boolean
)
