package com.bumper_car.vroomie_fe.data.remote

import com.bumper_car.vroomie_fe.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://${BuildConfig.SERVER_IP_ADDRESS}:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}