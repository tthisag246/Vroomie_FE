package com.bumper_car.vroomie_fe.data.remote.user

import com.google.gson.annotations.SerializedName

data class MonthlyDetailStat(
    @SerializedName("average_score")
    val averageScore: Int,

    @SerializedName("total_distance")
    val totalDistance: Float,

    @SerializedName("total_duration")
    val totalDuration: Int,

    @SerializedName("total_speeding_count")
    val totalSpeedingCount: Int,

    @SerializedName("total_sudden_acceleration_count")
    val totalSuddenAccelerationCount: Int,

    @SerializedName("total_sudden_deceleration_count")
    val totalSuddenDecelerationCount: Int,

    @SerializedName("total_safe_distance_violation_count")
    val totalSafeDistanceViolationCount: Int,

    @SerializedName("total_lane_deviation_right_count")
    val totalLaneDeviationRightCount: Int,

    @SerializedName("total_lane_deviation_left_count")
    val totalLaneDeviationLeftCount: Int
)

data class MonthlyScoreItem(
    @SerializedName("year")
    val year: Int,

    @SerializedName("month")
    val month: Int,

    @SerializedName("score")
    val score: Int
)

data class UserScoreReportResponse(
    @SerializedName("score")
    val score: Int,

    @SerializedName("percentile")
    val percentile: Float,

    @SerializedName("percentile_distribution")
    val percentileDistribution: Map<Int, Int>,

    @SerializedName("monthly_scores")
    val monthlyScores: List<MonthlyScoreItem>,

    @SerializedName("monthly_detail_stats")
    val monthlyDetailStats: Map<Int, MonthlyDetailStat>
)
