package com.bumper_car.vroomie_fe.data.remote.drivehistory

import com.google.gson.annotations.SerializedName

data class DriveHistoriesItem(
    @SerializedName("history_id")
    val historyId: Int,

    @SerializedName("start_at")
    val startAt: String,

    @SerializedName("end_at")
    val endAt: String,

    @SerializedName("start_location")
    val startLocation: String,

    @SerializedName("end_location")
    val endLocation: String,

    @SerializedName("score")
    val score: Int
)

data class DriveHistoriesResponse(
    @SerializedName("histories")
    val histories: List<DriveHistoriesItem>
)
