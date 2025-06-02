package com.bumper_car.vroomie_fe.data.remote.drivehistory

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
}