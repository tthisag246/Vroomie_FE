package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.auth.AuthRemoteDataSource
import com.bumper_car.vroomie_fe.data.remote.auth.GetMyInfoResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
): AuthRepository {
    override suspend fun getMyInfo(): GetMyInfoResponse {
        return remoteDataSource.getMyInfo()
    }
}