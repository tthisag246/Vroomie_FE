package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.drivehistory.DriveHistoryRemoteDataSource
import com.bumper_car.vroomie_fe.data.remote.drivehistory.DriveHistoryVideoItem
import com.bumper_car.vroomie_fe.domain.model.DriveFeedback
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import javax.inject.Inject

class DriveHistoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: DriveHistoryRemoteDataSource
) : DriveHistoryRepository {
    override suspend fun getDriveHistories(): List<DriveHistory> {
        return remoteDataSource.getDriveHistories().histories.map { response ->
            DriveHistory(
                historyId = response.historyId,
                startLocation = response.startLocation,
                endLocation = response.endLocation,
                startAt = response.startAt,
                endAt = response.endAt,
                score = response.score,
                distance = null,
                duration = null,
                laneDeviationLeftCount = null,
                laneDeviationRightCount = null,
                safeDistanceViolationCount = null,
                suddenDecelerationCount = null,
                suddenAccelerationCount = null,
                speedingCount = null,
                feedback = null
            )
        }
    }

    override suspend fun getDriveHistory(historyId: Int): DriveHistory {
        val history = remoteDataSource.getDriveHistory(historyId)
        return DriveHistory(
            historyId = historyId,
            startLocation = history.startLocation,
            endLocation = history.endLocation,
            startAt = history.startAt,
            endAt = history.endAt,
            score = history.score,
            distance = history.distance,
            duration = history.duration,
            laneDeviationLeftCount = history.laneDeviationLeftCount,
            laneDeviationRightCount = history.laneDeviationRightCount,
            safeDistanceViolationCount = history.safeDistanceViolationCount,
            suddenDecelerationCount = history.suddenDecelerationCount,
            suddenAccelerationCount = history.suddenAccelerationCount,
            speedingCount = history.speedingCount,
            feedback = history.videos.map { it.toDriveFeedback() }
        )
    }

    private fun DriveHistoryVideoItem.toDriveFeedback(): DriveFeedback {
        return DriveFeedback(
            title = title,
            content = content,
            videoUrl = url
        )
    }
}