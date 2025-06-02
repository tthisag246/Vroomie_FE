package com.bumper_car.vroomie_fe.data.remote.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user_name")
    val userName: String,

    @SerializedName("car_model")
    val carModel: String,

    @SerializedName("car_hipass")
    val carHipass: Boolean,

    @SerializedName("car_type")
    val carType: String,

    @SerializedName("car_fuel")
    val carFuel: String,

    @SerializedName("user_score")
    val userScore: Int,
)
