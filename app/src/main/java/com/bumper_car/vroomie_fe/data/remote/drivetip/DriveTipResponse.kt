package com.bumper_car.vroomie_fe.data.remote.drivetip

import com.google.gson.annotations.SerializedName

data class DriveTipResponse(
    @SerializedName("tip_id")
    val tipId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String,

    @SerializedName("create_at")
    val createAt: String,

    @SerializedName("content")
    val content: String
)
