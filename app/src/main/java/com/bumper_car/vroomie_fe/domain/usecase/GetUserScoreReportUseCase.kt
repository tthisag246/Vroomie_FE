package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.remote.user.UserScoreReportResponse
import com.bumper_car.vroomie_fe.data.repository.UserRepository
import jakarta.inject.Inject

class GetUserScoreReportUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): UserScoreReportResponse {
        return repository.getUserScoreReport()
    }
}