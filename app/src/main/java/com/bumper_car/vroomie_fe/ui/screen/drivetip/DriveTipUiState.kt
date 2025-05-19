package com.bumper_car.vroomie_fe.ui.screen.drivetip

data class DriveTipUiState(
    val tipList: List<DriveTipItemUiState> = emptyList()
)

data class DriveTipItemUiState(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val date: String
)