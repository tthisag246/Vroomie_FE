package com.bumper_car.vroomie_fe.ui.screen.drivescore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.usecase.GetUserScoreReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DriveScoreViewModel @Inject constructor(
    private val getUserScoreReportUseCase: GetUserScoreReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriveScoreUiState())
    val uiState: StateFlow<DriveScoreUiState> = _uiState.asStateFlow()

    fun fetchDriveScore() {
        viewModelScope.launch {
            try {
                val report = getUserScoreReportUseCase()
                _uiState.value = DriveScoreUiState(
                    score = report.score,
                    percentile = report.percentile.toInt(),
                    percentileDistribution = report.percentileDistribution,
                    monthlyScores = report.monthlyScores.map {
                        MonthlyScoreItemUiState(
                            year = it.year,
                            month = it.month,
                            score = it.score
                        )
                    },
                    monthlyDetailStatsUiState = report.monthlyDetailStats.mapValues { (_, stat) ->
                        DetailStatsUiState(
                            averageScore = stat.averageScore,
                            totalDistance = stat.totalDistance,
                            totalDuration = stat.totalDuration,
                            totalSpeedingCount = stat.totalSpeedingCount,
                            totalSuddenAccelerationCount = stat.totalSuddenAccelerationCount,
                            totalSuddenDecelerationCount = stat.totalSuddenDecelerationCount,
                            totalSafeDistanceViolationCount = stat.totalSafeDistanceViolationCount,
                            totalLaneDeviationRightCount = stat.totalLaneDeviationRightCount,
                            totalLaneDeviationLeftCount = stat.totalLaneDeviationLeftCount
                        )
                    },
                    selectedIndex = report.monthlyScores.sortedWith(compareBy({ it.year }, { it.month })).lastIndex
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSelectedIndex(index: Int) {
        _uiState.update { it.copy(selectedIndex = index) }
    }

}