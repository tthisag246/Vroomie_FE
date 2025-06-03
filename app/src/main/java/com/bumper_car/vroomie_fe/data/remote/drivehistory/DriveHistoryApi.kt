package com.bumper_car.vroomie_fe.data.remote.drivehistory

import retrofit2.http.GET
import retrofit2.http.Path

interface DriveHistoryApi {
    @GET("/drive/histories")
    suspend fun getDriveHistories(): DriveHistoriesResponse

    @GET("/drive/histories/{id}")
    suspend fun getDriveHistory(@Path("id") historyId: Int): DriveHistoryResponse
}