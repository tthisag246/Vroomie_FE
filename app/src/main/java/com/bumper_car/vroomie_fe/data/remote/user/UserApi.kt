package com.bumper_car.vroomie_fe.data.remote.user

import retrofit2.http.GET

interface UserApi {
    @GET("/users/score")
    suspend fun getUserScore(): UserScoreResponse

    @GET("/users")
    suspend fun getUser(): UserResponse

    @GET("/users/score/report")
    suspend fun getUserScoreReport(): UserScoreReportResponse
}