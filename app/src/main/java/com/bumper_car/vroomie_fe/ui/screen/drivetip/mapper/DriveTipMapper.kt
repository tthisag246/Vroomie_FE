package com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper

import com.bumper_car.vroomie_fe.domain.model.DriveTip
import com.bumper_car.vroomie_fe.ui.screen.drivetip.DriveTipDetailUiState
import com.bumper_car.vroomie_fe.ui.screen.drivetip.DriveTipItemUiState

fun DriveTip.toDriveTipItemUiState(): DriveTipItemUiState {
    return DriveTipItemUiState(
        id = id,
        title = title,
        thumbnailUrl = thumbnailUrl,
        date = date
    )
}

fun DriveTip.toDriveTipDetailUiState(): DriveTipDetailUiState {
    return DriveTipDetailUiState(
        id = id,
        title = title,
        thumbnailUrl = thumbnailUrl,
        date = date,
        content = content.orEmpty()
    )
}