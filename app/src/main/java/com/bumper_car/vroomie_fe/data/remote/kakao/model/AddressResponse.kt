package com.bumper_car.vroomie_fe.data.remote.kakao.model

data class AddressResponse(
    val documents: List<AddressDocument>
)

data class AddressDocument(
    val address_name: String,
    val x: String,
    val y: String
)
