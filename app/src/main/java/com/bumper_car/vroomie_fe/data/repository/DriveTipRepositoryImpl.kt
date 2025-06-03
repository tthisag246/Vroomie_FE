package com.bumper_car.vroomie_fe.data.repository

import com.bumper_car.vroomie_fe.data.remote.drivetip.DriveTipRemoteDataSource
import com.bumper_car.vroomie_fe.domain.model.DriveTip
import javax.inject.Inject

class DriveTipRepositoryImpl @Inject constructor(
    private val remoteDataSource: DriveTipRemoteDataSource
) : DriveTipRepository {
    override suspend fun getDriveTips(): List<DriveTip> {
        return remoteDataSource.getDriveTips().tips.map { response ->
            DriveTip(
                tipId = response.tipId,
                title = response.title,
                thumbnailUrl = response.thumbnailUrl,
                createAt = response.createAt,
                content = null
            )
        }
    }

    override suspend fun getDriveTipsTitle(): List<DriveTip> {
        return remoteDataSource.getDriveTips(fields = "title").tips.map { response ->
            DriveTip(
                tipId = response.tipId,
                title = response.title,
                thumbnailUrl = null,
                createAt = null,
                content = null
            )
        }
    }

    override suspend fun getDriveTip(tipId: Int): DriveTip {
        val response = remoteDataSource.getDriveTip(tipId)
        return DriveTip(
            tipId = response.tipId,
            title = response.title,
            thumbnailUrl = response.thumbnailUrl,
            createAt = response.createAt,
            content = response.content
        )
    }
}