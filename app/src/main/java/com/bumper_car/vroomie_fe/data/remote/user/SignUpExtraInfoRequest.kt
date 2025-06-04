package com.bumper_car.vroomie_fe.data.remote.user

data class SignUpExtraInfoRequest(
    val user_name: String,
    val car_model: String?,
    val car_hipass: Boolean?,
    val car_type: String?,
    val car_fuel: String?
)
