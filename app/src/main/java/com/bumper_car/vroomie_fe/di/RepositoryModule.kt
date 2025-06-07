package com.bumper_car.vroomie_fe.di

import com.bumper_car.vroomie_fe.data.remote.auth.AuthRemoteDataSource
import com.bumper_car.vroomie_fe.data.remote.drivehistory.DriveHistoryRemoteDataSource
import com.bumper_car.vroomie_fe.data.remote.drivetip.DriveTipRemoteDataSource
import com.bumper_car.vroomie_fe.data.remote.user.UserRemoteDataSource
import com.bumper_car.vroomie_fe.data.repository.AuthRepository
import com.bumper_car.vroomie_fe.data.repository.AuthRepositoryImpl
import com.bumper_car.vroomie_fe.data.repository.DriveHistoryRepository
import com.bumper_car.vroomie_fe.data.repository.DriveHistoryRepositoryImpl
import com.bumper_car.vroomie_fe.data.repository.DriveTipRepository
import com.bumper_car.vroomie_fe.data.repository.DriveTipRepositoryImpl
import com.bumper_car.vroomie_fe.data.repository.UserRepository
import com.bumper_car.vroomie_fe.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideDriveTipRepository(
        remote: DriveTipRemoteDataSource
    ): DriveTipRepository = DriveTipRepositoryImpl(remote)

    @Provides
    @Singleton
    fun provideUserRepository(
        remote: UserRemoteDataSource
    ): UserRepository = UserRepositoryImpl(remote)

    @Provides
    @Singleton
    fun provideDriveHistoryRepository(
        remote: DriveHistoryRemoteDataSource
    ): DriveHistoryRepository = DriveHistoryRepositoryImpl(remote)

    @Provides
    @Singleton
    fun provideAuthRepository(
        remote: AuthRemoteDataSource
    ): AuthRepository = AuthRepositoryImpl(remote)
}