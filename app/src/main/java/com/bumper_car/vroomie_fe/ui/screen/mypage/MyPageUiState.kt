package com.bumper_car.vroomie_fe.ui.screen.mypage

data class MyPageUiState(
    val nickname: String = "",
    val carModel: String = "",
    val carType: String = "",
    val hasHiPass: Boolean = false,
    val fuelType: String = "",
    val level: Int = 1
)