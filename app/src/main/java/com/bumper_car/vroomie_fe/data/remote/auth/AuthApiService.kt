package com.bumper_car.vroomie_fe.data.remote.auth

import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApiService {
    @GET("/me")
    suspend fun getMyInfo(
        @Header("Authorization") token: String
    ): GetMyInfoResponse
}

data class GetMyInfoResponse(
    val kakao_id: String,
    val user_name: String
)
