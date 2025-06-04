package com.bumper_car.vroomie_fe.ui.screen.drive.mapper

import com.bumper_car.vroomie_fe.domain.model.DriveFeedback
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import com.bumper_car.vroomie_fe.ui.screen.drive.DriveResultUiState
import com.bumper_car.vroomie_fe.ui.screen.drive.FeedbackUiState

fun DriveHistory.toDriveResultUiState(): DriveResultUiState {
    return DriveResultUiState(
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
        feedback = feedback?.map { it.toFeedbackUiState() } ?: emptyList()
    )
}

fun DriveFeedback.toFeedbackUiState(): FeedbackUiState {
    return FeedbackUiState(
        title = title,
        content = content,
        videoUrl = videoUrl
    )
}