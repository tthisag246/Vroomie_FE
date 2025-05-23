package com.bumper_car.vroomie_fe.data.remote.kakao.model

data class TranscoordResponse(
    val documents: List<CoordDocument>
)

data class CoordDocument(
    val x: Double, // TM x좌표
    val y: Double  // TM y좌표
)
