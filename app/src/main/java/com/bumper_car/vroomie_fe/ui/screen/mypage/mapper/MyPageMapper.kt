package com.bumper_car.vroomie_fe.ui.screen.mypage.mapper

import com.bumper_car.vroomie_fe.domain.model.User
import com.bumper_car.vroomie_fe.ui.screen.mypage.MyPageUiState

fun User.toMyPageUiState(): MyPageUiState {
    return MyPageUiState(
        userName = userName.orEmpty(),
        carModel = carModel.orEmpty(),
        carHipass = carHipass,
        carType = carType,
        carFuel = carFuel,
        userScore = userScore
    )
}
