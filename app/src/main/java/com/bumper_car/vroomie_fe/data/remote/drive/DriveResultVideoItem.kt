package com.bumper_car.vroomie_fe.data.remote.drive

import com.google.gson.annotations.SerializedName

data class DriveResultVideoItem(
    @SerializedName("video_id")
    val videoId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("url")
    val url: String
)