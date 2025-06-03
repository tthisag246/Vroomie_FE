package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.domain.model.DriveTip

interface DriveTipRepository {
    suspend fun getDriveTips(): List<DriveTip>
    suspend fun getDriveTipsTitle(): List<DriveTip>
    suspend fun getDriveTip(tipId: Int): DriveTip
}