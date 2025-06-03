package com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper

import com.bumper_car.vroomie_fe.domain.model.DriveTip
import com.bumper_car.vroomie_fe.ui.screen.drivetip.DriveTipDetailUiState
import com.bumper_car.vroomie_fe.ui.screen.drivetip.DriveTipItemUiState

fun DriveTip.toDriveTipItemUiState(): DriveTipItemUiState {
    return DriveTipItemUiState(
        tipId = tipId,
        title = title,
        thumbnailUrl = thumbnailUrl.orEmpty(),
        createAt = createAt.orEmpty()
    )
}

fun DriveTip.toDriveTipDetailUiState(): DriveTipDetailUiState {
    return DriveTipDetailUiState(
        tipId = tipId,
        title = title,
        thumbnailUrl = thumbnailUrl.orEmpty(),
        createAt = createAt.orEmpty(),
        content = content.orEmpty()
    )
}