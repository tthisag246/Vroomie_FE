package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.remote.auth.GetMyInfoResponse
import com.bumper_car.vroomie_fe.data.repository.AuthRepository
import jakarta.inject.Inject

class GetMyInfoUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): GetMyInfoResponse {
        return repository.getMyInfo()
    }
}