package com.bumper_car.vroomie_fe.data.remote

import com.bumper_car.vroomie_fe.data.local.TokenPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenPreferences: TokenPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenPreferences.tokenFlow.value

        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}
