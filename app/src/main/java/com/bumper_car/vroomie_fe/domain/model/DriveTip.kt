package com.bumper_car.vroomie_fe.domain.model


data class DriveTip(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val date: String,
    val content: String = ""
)