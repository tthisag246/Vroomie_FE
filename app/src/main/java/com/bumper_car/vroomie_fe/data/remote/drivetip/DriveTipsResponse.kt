package com.bumper_car.vroomie_fe.data.remote.drivetip

import com.google.gson.annotations.SerializedName

data class DriveTipsItem(
    @SerializedName("tip_id")
    val tipId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,

    @SerializedName("create_at")
    val createAt: String? = null
)

data class DriveTipsResponse(
    @SerializedName("tips")
    val tips: List<DriveTipsItem>
)
