package com.bumper_car.vroomie_fe.data.remote.auth

import retrofit2.http.GET
import retrofit2.http.Header

interface MeApiService {
    @GET("/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): MeResponse
}

data class MeResponse(
    val kakao_id: String,
    val nickname: String
)