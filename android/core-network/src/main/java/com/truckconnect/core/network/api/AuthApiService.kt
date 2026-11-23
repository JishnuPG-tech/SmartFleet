package com.truckconnect.core.network.api

import com.truckconnect.core.network.model.AdminLoginRequest
import com.truckconnect.core.network.model.AuthResponse
import com.truckconnect.core.network.model.DriverLoginRequest
import com.truckconnect.core.network.model.DriverRegisterRequest
import com.truckconnect.core.network.model.RegisterResponse
import com.truckconnect.core.network.model.AppVersionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("auth/login")
    suspend fun loginAdmin(
        @Body request: AdminLoginRequest,
        @Header("client-request-id") requestId: String
    ): Response<AuthResponse>

    @POST("auth/driver/login-pin")
    suspend fun loginDriver(
        @Body request: DriverLoginRequest,
        @Header("client-request-id") requestId: String
    ): Response<AuthResponse>

    @POST("auth/driver/register")
    suspend fun registerDriver(
        @Body request: DriverRegisterRequest,
        @Header("client-request-id") requestId: String
    ): Response<RegisterResponse>

    @GET("app/version")
    suspend fun checkVersion(
        @Query("flavor") flavor: String,
        @Query("versionCode") versionCode: Int
    ): Response<AppVersionResponse>
}
