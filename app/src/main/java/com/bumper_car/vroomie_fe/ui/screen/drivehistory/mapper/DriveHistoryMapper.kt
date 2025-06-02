package com.bumper_car.vroomie_fe.ui.screen.drivehistory.mapper

import com.bumper_car.vroomie_fe.domain.model.DriveFeedback
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.DriveHistoryDetailUiState
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.DriveHistoryItemUiState
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.FeedbackUiState

fun DriveHistory.toDriveHistoryItemUiState(): DriveHistoryItemUiState {
    return DriveHistoryItemUiState(
        historyId = historyId,
        startLocation = startLocation,
        endLocation = endLocation,
        startAt = startAt,
        score = score
    )
}

fun DriveHistory.toDriveHistoryDetailUiState(): DriveHistoryDetailUiState {
    return DriveHistoryDetailUiState(
        startAt = startAt,
        endAt = endAt,
        startLocation = startLocation,
        endLocation = endLocation,
        distance = distance ?: 0f,
        duration = duration ?: 0,
        score = score,
        laneDeviationLeftCount = laneDeviationLeftCount ?: 0,
        laneDeviationRightCount = laneDeviationRightCount ?: 0,
        safeDistanceViolationCount = safeDistanceViolationCount ?: 0,
        suddenDecelerationCount = suddenDecelerationCount ?: 0,
        suddenAccelerationCount = suddenAccelerationCount ?: 0,
        speedingCount = speedingCount ?: 0,
        feedback = feedback?.map { it.toDriveFeedbackUiState() } ?: emptyList()
    )
}

fun DriveFeedback.toDriveFeedbackUiState(): FeedbackUiState {
    return FeedbackUiState(
        title = title,
        content = content,
        videoUrl = videoUrl
    )
}