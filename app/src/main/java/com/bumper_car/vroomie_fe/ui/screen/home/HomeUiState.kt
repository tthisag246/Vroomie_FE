package com.bumper_car.vroomie_fe.ui.screen.home

data class HomeUiState(
    val query: String = "",
    val isSearchMode: Boolean = false,
    val driveScore: Int = 63,
    val searchHistory: List<String> = listOf(),
    val driveInformations: List<String> = listOf(),
)