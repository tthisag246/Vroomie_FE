package com.bumper_car.vroomie_fe.data.remote.drive

import com.google.gson.annotations.SerializedName

data class DriveResultRequest(
    @SerializedName("start_at")
    val startAt: String,

    @SerializedName("end_at")
    val endAt: String,

    @SerializedName("start_location")
    val startLocation: String,

    @SerializedName("end_location")
    val endLocation: String,

    @SerializedName("distance")
    val distance: Float,

    @SerializedName("duration")
    val duration: Int,

    @SerializedName("lane_deviation_left_count")
    val laneDeviationLeftCount: Int,

    @SerializedName("lane_deviation_right_count")
    val laneDeviationRightCount: Int,

    @SerializedName("safe_distance_violation_count")
    val safeDistanceViolationCount: Int,

    @SerializedName("sudden_deceleration_count")
    val suddenDecelerationCount: Int,

    @SerializedName("sudden_acceleration_count")
    val suddenAccelerationCount: Int,

    @SerializedName("speeding_count")
    val speedingCount: Int,

    @SerializedName("videos")
    val videos: List<DriveResultVideoItem>
)
