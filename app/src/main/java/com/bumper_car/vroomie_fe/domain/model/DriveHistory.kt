package com.bumper_car.vroomie_fe.domain.model

data class DriveHistory(
    val historyId: Int,
    val startAt: String,
    val endAt: String,
    val startLocation: String,
    val endLocation: String,
    val score: Int,
    val distance: Float? = null,
    val duration: Int? = null,
    val laneDeviationLeftCount: Int? = null,
    val laneDeviationRightCount: Int? = null,
    val safeDistanceViolationCount: Int? = null,
    val suddenDecelerationCount: Int? = null,
    val suddenAccelerationCount: Int? = null,
    val speedingCount: Int? = null,
    val feedback: List<DriveFeedback>? = null
)

data class DriveFeedback(
    val title: String,
    val content: String,
    val videoUrl: String
)