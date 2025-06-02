package com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper

import com.bumper_car.vroomie_fe.domain.model.DriveTip
import com.bumper_car.vroomie_fe.ui.screen.home.DriveTipItemUiState

fun DriveTip.toDriveTipTitleUiState(): DriveTipItemUiState {
    return DriveTipItemUiState(
        tipId = tipId,
        title = title
    )
}
