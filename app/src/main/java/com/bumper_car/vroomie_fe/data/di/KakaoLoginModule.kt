package com.bumper_car.vroomie_fe.data.di

import android.util.Log
import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.data.remote.kakao.KakaoNaviApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KakaoLoginModule {

    private const val BASE_URL = "https://dapi.kakao.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val authKey = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}"
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", authKey)
                    .build()

                Log.d("NaviDebug", "🚨 최종 Authorization 헤더: $authKey")

                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @Named("KakaoRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideKakaoLocalApiService(retrofit: Retrofit): KakaoNaviApi {
        return retrofit.create(KakaoNaviApi::class.java)
    }
}
