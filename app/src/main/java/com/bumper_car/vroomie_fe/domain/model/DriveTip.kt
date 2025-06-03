package com.bumper_car.vroomie_fe.domain.model


data class DriveTip(
    val tipId: Int,
    val title: String,
    val thumbnailUrl: String?,
    val createAt: String?,
    val content: String?
)