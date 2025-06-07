package com.bumper_car.vroomie_fe.data.remote.auth

import retrofit2.http.GET

interface AuthApi {
    @GET("/auth/me")
    suspend fun getMyInfo(): GetMyInfoResponse
}