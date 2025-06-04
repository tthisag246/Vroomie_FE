package com.bumper_car.vroomie_fe.data.remote.kakao

import com.bumper_car.vroomie_fe.data.remote.kakao.model.AddressResponse
import com.bumper_car.vroomie_fe.data.remote.kakao.model.KakaoCoord2AddressResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoNaviApi {
    @GET("v2/local/search/keyword.json")
    suspend fun getAddressFromQuery(@Query("query") query: String): AddressResponse

    @GET("v2/local/geo/coord2address.json")
    suspend fun getAddressFromCoordinates(
        @Header("Authorization") authorization: String,
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Query("input_coord") inputCoord: String = "WGS84"
    ): KakaoCoord2AddressResponse
}

