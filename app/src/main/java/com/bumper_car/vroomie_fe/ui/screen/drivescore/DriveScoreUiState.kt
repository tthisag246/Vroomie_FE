package com.bumper_car.vroomie_fe.ui.screen.drivescore

data class DriveScoreUiState(
    val driveScore: Int = 0,
    val rankPercent: Int = 0,
    val months: List<String> = emptyList(),
    val scores: List<Int> = emptyList(),
    val driveStatsList: List<DriveStats> = emptyList(),
    val selectedIndex: Int = 0
)

data class DriveStats(
    val totalDistance: String,
    val totalTime: String,
    val overspeed: String,
    val harshBraking: String,
    val harshAccel: String,
    val closeFollowing: String,
    val laneDeviation: String
)