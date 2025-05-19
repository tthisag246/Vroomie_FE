package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import com.bumper_car.vroomie_fe.domain.model.DriveFeedback

data class DriveHistoryDetailUiState(
    val id: Int = 0,
    val startLocation: String = "",
    val endLocation: String = "",
    val date: String = "",
    val score: Int = 0,
    val distance: Int = 0,
    val duration: String = "",
    val laneDeviationLeftCount: Int = 0,
    val laneDeviationRightCount: Int = 0,
    val safeDistanceViolationCount: Int = 0,
    val suddenDecelerationCount: Int = 0,
    val suddenAccelerationCount: Int = 0,
    val speedingCount: Int = 0,
    val feedback: List<DriveFeedback> = emptyList()
)