package com.bumper_car.vroomie_fe.ui.screen.mypage

data class MyPageUiState(
    val userName: String = "",
    val carModel: String = "",
    val carHipass: Boolean? = false,
    val carType: String? = "",
    val carFuel: String? = "",
    val userScore: Int = 0
)