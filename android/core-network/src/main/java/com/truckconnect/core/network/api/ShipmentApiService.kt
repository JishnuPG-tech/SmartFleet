package com.truckconnect.core.network.api

import com.truckconnect.core.network.model.AssignShipmentRequest
import com.truckconnect.core.network.model.CompleteDeliveryRequest
import com.truckconnect.core.network.model.CreateShipmentRequest
import com.truckconnect.core.network.model.RejectShipmentRequest
import com.truckconnect.core.network.model.ShipmentDetailDto
import com.truckconnect.core.network.model.ShipmentDto
import com.truckconnect.core.network.model.UpdateMilestoneRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ShipmentApiService {

    @GET("shipments")
    suspend fun getShipments(
        @Query("status") status: String? = null
    ): Response<List<ShipmentDto>>

    @GET("shipments/{id}")
    suspend fun getShipmentById(
        @Path("id") id: String
    ): Response<ShipmentDetailDto>

    @POST("shipments")
    suspend fun createShipment(
        @Body request: CreateShipmentRequest
    ): Response<ShipmentDetailDto>

    @POST("shipments/{id}/assign")
    suspend fun assignShipment(
        @Path("id") id: String,
        @Body request: AssignShipmentRequest
    ): Response<ShipmentDetailDto>

    @POST("shipments/{id}/accept")
    suspend fun acceptShipment(
        @Path("id") id: String
    ): Response<ShipmentDetailDto>

    @POST("shipments/{id}/reject")
    suspend fun rejectShipment(
        @Path("id") id: String,
        @Body request: RejectShipmentRequest
    ): Response<ShipmentDetailDto>

    @POST("shipments/{id}/milestone")
    suspend fun updateMilestone(
        @Path("id") id: String,
        @Body request: UpdateMilestoneRequest
    ): Response<ShipmentDetailDto>

    @POST("shipments/{id}/complete")
    suspend fun completeDelivery(
        @Path("id") id: String,
        @Body request: CompleteDeliveryRequest
    ): Response<ShipmentDetailDto>
}
