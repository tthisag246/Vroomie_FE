package com.bumper_car.vroomie_fe.data.remote.drivehistory

import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultResponse
import javax.inject.Inject

class DriveHistoryRemoteDataSource @Inject constructor(
    private val api: DriveHistoryApi
) {
    suspend fun getDriveHistories(): DriveHistoriesResponse {
        return api.getDriveHistories()
    }

    suspend fun getDriveHistory(historyId: Int): DriveHistoryResponse {
        return api.getDriveHistory(historyId)
    }

    suspend fun saveDriveResult(driveResultRequest: DriveResultRequest): DriveResultResponse {
        return api.saveDriveResult(driveResultRequest)
    }
}