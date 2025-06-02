package com.bumper_car.vroomie_fe.ui.screen.drivetip

data class DriveTipUiState(
    val tips: List<DriveTipItemUiState> = emptyList()
)

data class DriveTipItemUiState(
    val tipId: Int,
    val title: String,
    val thumbnailUrl: String,
    val createAt: String
)