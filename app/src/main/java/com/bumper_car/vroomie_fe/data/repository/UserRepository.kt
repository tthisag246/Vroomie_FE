package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.user.SignUpExtraInfoRequest
import com.bumper_car.vroomie_fe.data.remote.user.UserScoreReportResponse
import com.bumper_car.vroomie_fe.domain.model.User

interface UserRepository {
    suspend fun getUserScore(): Int
    suspend fun getUserInfo(): User
    suspend fun getUserScoreReport(): UserScoreReportResponse
    suspend fun saveUserInfo(signUpExtraInfoRequest: SignUpExtraInfoRequest): Boolean
}