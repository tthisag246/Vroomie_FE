package com.bumper_car.vroomie_fe.data.remote.drivetip

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DriveTipApi {
    @GET("/drive/tips")
    suspend fun getDriveTips(
        @Query("fields") field: String? = null
    ): DriveTipsResponse

    @GET("/drive/tips/{id}")
    suspend fun getDriveTip(@Path("id") tipId: Int): DriveTipResponse
}