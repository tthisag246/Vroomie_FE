package com.bumper_car.vroomie_fe.data.remote.auth

import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultResponse
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val api: AuthApi
) {
    suspend fun getMyInfo(): GetMyInfoResponse {
        return api.getMyInfo()
    }
}