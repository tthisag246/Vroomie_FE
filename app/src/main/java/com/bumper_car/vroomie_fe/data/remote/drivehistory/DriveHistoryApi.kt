package com.bumper_car.vroomie_fe.data.remote.drivehistory

import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DriveHistoryApi {
    @GET("/drive/histories")
    suspend fun getDriveHistories(): DriveHistoriesResponse

    @GET("/drive/histories/{id}")
    suspend fun getDriveHistory(@Path("id") historyId: Int): DriveHistoryResponse

    @POST("/drive/histories")
    suspend fun saveDriveResult(@Body driveResultRequest: DriveResultRequest): DriveResultResponse
}