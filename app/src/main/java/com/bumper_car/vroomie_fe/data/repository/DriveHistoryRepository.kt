package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.domain.model.DriveHistory

interface DriveHistoryRepository {
    suspend fun getDriveHistories(): List<DriveHistory>
    suspend fun getDriveHistory(historyId: Int): DriveHistory
    suspend fun saveDriveResult(driveResultRequest: DriveResultRequest): DriveHistory
}