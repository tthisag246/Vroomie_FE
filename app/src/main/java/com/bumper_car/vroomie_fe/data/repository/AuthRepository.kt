package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.auth.GetMyInfoResponse

interface AuthRepository {
    suspend fun getMyInfo(): GetMyInfoResponse
}