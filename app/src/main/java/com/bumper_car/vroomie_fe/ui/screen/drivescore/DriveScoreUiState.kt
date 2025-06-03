package com.bumper_car.vroomie_fe.ui.screen.drivescore

data class DriveScoreUiState(
    val score: Int = 0,
    val percentile: Int = 0,
    val percentileDistribution: Map<Int, Int> = emptyMap(),
    val monthlyScores: List<MonthlyScoreItemUiState> = emptyList(),
    val monthlyDetailStatsUiState: Map<Int, DetailStatsUiState> = emptyMap(),
    val selectedIndex: Int = 0
)

data class MonthlyScoreItemUiState(
    val year: Int,
    val month: Int,
    val score: Int
)

data class DetailStatsUiState(
    val averageScore: Int,
    val totalDistance: Float,
    val totalDuration: Int,
    val totalSpeedingCount: Int,
    val totalSuddenAccelerationCount: Int,
    val totalSuddenDecelerationCount: Int,
    val totalSafeDistanceViolationCount: Int,
    val totalLaneDeviationRightCount: Int,
    val totalLaneDeviationLeftCount: Int
)