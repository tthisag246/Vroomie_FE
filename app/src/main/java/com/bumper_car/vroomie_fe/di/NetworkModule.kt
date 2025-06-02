package com.bumper_car.vroomie_fe.di

import com.bumper_car.vroomie_fe.BuildConfig
import com.bumper_car.vroomie_fe.data.remote.drivehistory.DriveHistoryApi
import com.bumper_car.vroomie_fe.data.remote.drivetip.DriveTipApi
import com.bumper_car.vroomie_fe.data.remote.user.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://" + BuildConfig.SERVER_IP_ADDRESS + ":8080"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
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
}