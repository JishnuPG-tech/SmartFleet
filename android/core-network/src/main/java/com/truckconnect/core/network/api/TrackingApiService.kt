package com.truckconnect.core.network.api

import com.truckconnect.core.network.model.BatchPingsRequest
import com.truckconnect.core.network.model.LiveVehicleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TrackingApiService {

    @POST("tracking/pings")
    suspend fun ingestPings(
        @Body request: BatchPingsRequest
    ): Response<Unit>

    @GET("tracking/live")
    suspend fun getLiveFleet(): Response<List<LiveVehicleDto>>
}
