package com.bumper_car.vroomie_fe.ui.screen.drivehistory

data class DriveHistoryUiState(
    val histories: List<DriveHistoryItemUiState> = emptyList()
)

data class DriveHistoryItemUiState(
    val historyId: Int,
    val startLocation: String,
    val endLocation: String,
    val startAt: String,
    val score: Int
)