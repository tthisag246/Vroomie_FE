package com.bumper_car.vroomie_fe.data.remote.kakao

import com.bumper_car.vroomie_fe.data.remote.kakao.model.AddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLocalApiService {
    @GET("v2/local/search/keyword.json")
    suspend fun getAddressFromQuery(@Query("query") query: String): AddressResponse
}

