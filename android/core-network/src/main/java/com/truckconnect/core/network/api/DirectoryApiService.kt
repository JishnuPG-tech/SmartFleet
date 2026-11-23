package com.truckconnect.core.network.api

import com.truckconnect.core.network.model.DriverDto
import com.truckconnect.core.network.model.DriverProfileDto
import com.truckconnect.core.network.model.RejectDriverRequest
import com.truckconnect.core.network.model.VehicleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DirectoryApiService {

    @GET("drivers")
    suspend fun getDrivers(
        @Query("availability") availability: String? = null,
        @Query("search") search: String? = null
    ): Response<List<DriverDto>>

    @GET("drivers/{id}")
    suspend fun getDriverById(
        @Path("id") id: String
    ): Response<DriverProfileDto>

    @GET("admin/drivers/pending")
    suspend fun getPendingApprovals(): Response<List<DriverProfileDto>>

    @POST("admin/drivers/{id}/approve")
    suspend fun approveDriver(
        @Path("id") id: String
    ): Response<DriverProfileDto>

    @POST("admin/drivers/{id}/reject")
    suspend fun rejectDriver(
        @Path("id") id: String,
        @Body request: RejectDriverRequest
    ): Response<DriverProfileDto>

    @GET("vehicles")
    suspend fun getVehicles(
        @Query("status") status: String? = null
    ): Response<List<VehicleDto>>
}
