package com.bumper_car.vroomie_fe.domain.model

data class User(
    val userId: Int?,
    val kakaoId: String?,
    val userName: String?,
    val createAt: String?,
    val carModel: String?,
    val carHipass: Boolean?,
    val carType: String?,
    val carFuel: String?,
    val userScore: Int
)