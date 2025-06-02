package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.repository.UserRepository
import jakarta.inject.Inject

class GetUserScoreUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getUserScore()
    }
}