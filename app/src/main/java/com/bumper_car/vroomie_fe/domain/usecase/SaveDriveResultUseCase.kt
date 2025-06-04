package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.data.repository.DriveHistoryRepository
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import jakarta.inject.Inject

class SaveDriveResultUseCase @Inject constructor(
    private val repository: DriveHistoryRepository
) {
    suspend operator fun invoke(driveResultRequest: DriveResultRequest): DriveHistory {
        return repository.saveDriveResult(driveResultRequest)
    }
}
