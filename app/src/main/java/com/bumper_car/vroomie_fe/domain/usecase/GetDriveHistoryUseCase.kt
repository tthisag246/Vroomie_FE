package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.repository.DriveHistoryRepository
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import jakarta.inject.Inject

class GetDriveHistoryUseCase @Inject constructor(
    private val repository: DriveHistoryRepository
) {
    suspend operator fun invoke(historyId: Int): DriveHistory {
        return repository.getDriveHistory(historyId)
    }
}