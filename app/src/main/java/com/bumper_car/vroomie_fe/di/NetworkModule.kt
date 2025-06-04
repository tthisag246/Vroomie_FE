package com.bumper_car.vroomie_fe.di

import android.util.Log
import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.data.local.TokenPreferences
import com.bumper_car.vroomie_fe.data.remote.AuthInterceptor
import com.bumper_car.vroomie_fe.data.remote.drivehistory.DriveHistoryApi
import com.bumper_car.vroomie_fe.data.remote.drivetip.DriveTipApi
import com.bumper_car.vroomie_fe.data.remote.kakao.KakaoNaviApi
import com.bumper_car.vroomie_fe.data.remote.user.UserApi
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
object NetworkModule {

    private const val BASE_URL = "http://" + BuildConfig.SERVER_IP_ADDRESS + ":8080"

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenPreferences: TokenPreferences): AuthInterceptor {
        return AuthInterceptor(tokenPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDriveTipApi(retrofit: Retrofit): DriveTipApi {
        return retrofit.create(DriveTipApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDriveHistoryApi(retrofit: Retrofit): DriveHistoryApi {
        return retrofit.create(DriveHistoryApi::class.java)
    }

    @Provides
    @Singleton
    @Named("KakaoInterceptor")
    fun provideKakaoInterceptor(): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val authKey = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}"
            val request = chain.request().newBuilder()
                .addHeader("Authorization", authKey)
                .build()

            Log.d("NaviDebug", "🚨 최종 Authorization 헤더: $authKey")

            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    @Named("KakaoRetrofit")
    fun provideKakaoRetrofit(
        okHttpClient: OkHttpClient,
        @Named("KakaoInterceptor") kakaoInterceptor: okhttp3.Interceptor
    ): Retrofit {
        val kakaoOkHttpClient = okHttpClient.newBuilder()
            .addInterceptor(kakaoInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .client(kakaoOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideKakaoLocalApi(@Named("KakaoRetrofit") retrofit: Retrofit): KakaoNaviApi {
        return retrofit.create(KakaoNaviApi::class.java)
    }
}