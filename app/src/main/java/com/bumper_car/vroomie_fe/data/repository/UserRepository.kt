package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.domain.model.User

interface UserRepository {
    suspend fun getUserScore(): Int
    suspend fun getUserInfo(): User
}