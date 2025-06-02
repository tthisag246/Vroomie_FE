package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.repository.DriveTipRepository
import com.bumper_car.vroomie_fe.domain.model.DriveTip
import jakarta.inject.Inject

class GetDriveTipUseCase @Inject constructor(
    private val repository: DriveTipRepository
) {
    suspend operator fun invoke(tipId: Int): DriveTip {
        return repository.getDriveTip(tipId)
    }
}