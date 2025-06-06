package com.bumper_car.vroomie_fe.data.remote.user

import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val api: UserApi
) {

    suspend fun getUserScore(): UserScoreResponse {
        return api.getUserScore()
    }

    suspend fun getUserInfo(): UserResponse {
        return api.getUser()
    }

    suspend fun getUserScoreReport(): UserScoreReportResponse {
        return api.getUserScoreReport()
    }

    suspend fun saveUserInfo(signUpExtraInfoRequest: SignUpExtraInfoRequest): Boolean {
        return api.saveUserInfo(signUpExtraInfoRequest).isSuccessful
    }
}