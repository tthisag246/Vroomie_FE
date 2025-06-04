package com.bumper_car.vroomie_fe.data.remote

import com.bumper_car.vroomie_fe.data.local.TokenPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenPreferences: TokenPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // ✅ runBlocking을 사용해 실제 저장소에서 토큰 조회
        val token = runBlocking { tokenPreferences.getToken() }

        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}
