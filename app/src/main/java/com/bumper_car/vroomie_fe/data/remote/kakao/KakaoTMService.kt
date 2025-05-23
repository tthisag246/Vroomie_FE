package com.bumper_car.vroomie_fe.data.remote.kakao

import com.bumper_car.vroomie_fe.data.remote.kakao.model.TranscoordResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoTMService {

    @GET("v2/local/geo/transcoord.json")
    suspend fun convertWgsToTm(
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Query("input_coord") input: String = "WGS84",
        @Query("output_coord") output: String = "TM"
    ): TranscoordResponse
}