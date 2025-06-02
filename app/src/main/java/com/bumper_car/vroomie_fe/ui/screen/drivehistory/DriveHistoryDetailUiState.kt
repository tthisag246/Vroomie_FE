package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import com.bumper_car.vroomie_fe.domain.model.DriveFeedback

data class DriveHistoryDetailUiState(
    val historyId: Int = 0,
    val startLocation: String = "",
    val endLocation: String = "",
    val startAt: String = "",
    val endAt: String = "",
    val score: Int = 0,
    val distance: Float = 0f,
    val duration: Int = 0,
    val laneDeviationLeftCount: Int = 0,
    val laneDeviationRightCount: Int = 0,
    val safeDistanceViolationCount: Int = 0,
    val suddenDecelerationCount: Int = 0,
    val suddenAccelerationCount: Int = 0,
    val speedingCount: Int = 0,
    val feedback: List<FeedbackUiState> = emptyList()
)

data class FeedbackUiState(
    val feedbackId: Int = 0,
    val title: String = "",
    val content: String = "",
    val videoUrl: String = ""
)