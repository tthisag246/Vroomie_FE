package com.bumper_car.vroomie_fe.data.remote.user

import com.google.gson.annotations.SerializedName

data class UserScoreResponse(
    @SerializedName("score")
    val score: Int,
)
