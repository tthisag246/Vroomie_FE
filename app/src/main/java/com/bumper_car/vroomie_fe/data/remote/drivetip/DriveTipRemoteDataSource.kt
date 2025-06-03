package com.bumper_car.vroomie_fe.data.remote.drivetip

import javax.inject.Inject

class DriveTipRemoteDataSource @Inject constructor(
    private val api: DriveTipApi
) {
    suspend fun getDriveTips(fields: String? = null): DriveTipsResponse {
        return api.getDriveTips(fields)
    }

    suspend fun getDriveTip(tipId: Int): DriveTipResponse {
        return api.getDriveTip(tipId)
    }
}