package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.repository.UserRepository
import com.bumper_car.vroomie_fe.domain.model.User
import jakarta.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): User {
        return repository.getUserInfo()
    }
}