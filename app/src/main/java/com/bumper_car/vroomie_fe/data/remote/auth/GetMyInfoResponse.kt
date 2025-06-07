package com.bumper_car.vroomie_fe.data.remote.auth

import com.google.gson.annotations.SerializedName

data class GetMyInfoResponse(
    @SerializedName("kakao_id")
    val kakaoId: String,

    @SerializedName("user_name")
    val userName: String
)
