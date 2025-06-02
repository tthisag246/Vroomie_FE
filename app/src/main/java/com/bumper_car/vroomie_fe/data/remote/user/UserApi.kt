package com.bumper_car.vroomie_fe.data.remote.user

import retrofit2.http.GET

interface UserApi {
    @GET("/users/drive/score")
    suspend fun getUserScore(): UserScoreResponse

    @GET("/users")
    suspend fun getUser(): UserResponse
}