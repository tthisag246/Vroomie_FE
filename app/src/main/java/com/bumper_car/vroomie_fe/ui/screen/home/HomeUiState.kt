package com.bumper_car.vroomie_fe.ui.screen.home

data class HomeUiState(
    val query: String = "",
    val isSearchMode: Boolean = false,
    val driveScore: Int = 0,
    val searchHistory: List<String> = listOf(),
    val driveTips: List<DriveTipItemUiState> = listOf(),

    // 목적지 정보
    val navigationLat: String? = null,
    val navigationLng: String? = null,
    val navigationPlaceName: String? = null
)

data class DriveTipItemUiState(
    val tipId: Int,
    val title: String
)