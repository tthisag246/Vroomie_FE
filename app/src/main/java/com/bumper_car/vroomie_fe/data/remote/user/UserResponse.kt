package com.bumper_car.vroomie_fe.data.remote.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user_name")
    val userName: String,

    @SerializedName("car_model")
    val carModel: String? = null,

    @SerializedName("car_hipass")
    val carHipass: Boolean? = null,

    @SerializedName("car_type")
    val carType: String? = null,

    @SerializedName("car_fuel")
    val carFuel: String? = null,

    @SerializedName("user_score")
    val userScore: Int,
)
