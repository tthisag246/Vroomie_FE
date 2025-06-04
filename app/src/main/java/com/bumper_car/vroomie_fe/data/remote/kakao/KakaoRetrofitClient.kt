package com.bumper_car.vroomie_fe.data.remote.kakao

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoRetrofitClient {
    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"

    val kakaoNaviApi: KakaoNaviApi by lazy {
        Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KakaoNaviApi::class.java)
    }
}