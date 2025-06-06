package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.user.SignUpExtraInfoRequest
import com.bumper_car.vroomie_fe.data.remote.user.UserRemoteDataSource
import com.bumper_car.vroomie_fe.data.remote.user.UserScoreReportResponse
import com.bumper_car.vroomie_fe.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun getUserScore(): Int {
        return remoteDataSource.getUserScore().score
    }

    override suspend fun getUserInfo(): User {
        val userResponse = remoteDataSource.getUserInfo()
        return User(
            userName = userResponse.userName,
            carModel = userResponse.carModel,
            carHipass = userResponse.carHipass,
            carType = userResponse.carType,
            carFuel = userResponse.carFuel,
            userScore = userResponse.userScore,
            userId = null,
            kakaoId = null,
            createAt = null,
        )
    }

    override suspend fun getUserScoreReport(): UserScoreReportResponse {
        return remoteDataSource.getUserScoreReport()
    }

    override suspend fun saveUserInfo(signUpExtraInfoRequest: SignUpExtraInfoRequest): Boolean {
        return remoteDataSource.saveUserInfo(signUpExtraInfoRequest)
    }
}