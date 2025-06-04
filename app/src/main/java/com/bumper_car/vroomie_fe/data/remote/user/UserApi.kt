package com.bumper_car.vroomie_fe.data.remote.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("/users/score")
    suspend fun getUserScore(): UserScoreResponse

    @GET("/users")
    suspend fun getUser(): UserResponse

    @GET("/users/score/report")
    suspend fun getUserScoreReport(): UserScoreReportResponse

    @POST("/users/extra")
    suspend fun saveUserInfo(
        @Body signUpExtraInfoRequest: SignUpExtraInfoRequest
    ): Response<Unit>
}