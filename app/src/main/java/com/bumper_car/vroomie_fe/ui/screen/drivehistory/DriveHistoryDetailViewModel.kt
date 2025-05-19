package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.model.DriveFeedback
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveHistoryDetailViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DriveHistoryDetailUiState())
    val uiState: StateFlow<DriveHistoryDetailUiState> = _uiState.asStateFlow()

    fun loadDriveHistoryById(id: Int) {
        viewModelScope.launch {
            val data = DriveHistory(
                id = id,
                startLocation = "서울역",
                endLocation = "강남역",
                date = "2025-05-18",
                score = 82,
                distance = 7000,
                duration = 92,
                laneDeviationLeftCount = 1,
                laneDeviationRightCount = 1,
                safeDistanceViolationCount = 2,
                suddenDecelerationCount = 1,
                suddenAccelerationCount = 1,
                speedingCount = 0,
                feedback = listOf(
                    DriveFeedback(
                        title = "차선 치우침을 주의하세요",
                        content = "오른쪽 다리가 차선의 중심에 가도록 신경써보세요!",
                        videoUrl = "https://your.server.com/video.mp4"
                    ),
                    DriveFeedback(
                        title = "차선 치우침을 주의하세요",
                        content = "오른쪽 다리가 차선의 중심에 가도록 신경써보세요!",
                        videoUrl = "https://your.server.com/video.mp4"
                    )
                )
            )
            _uiState.value = data.toUiState()
        }
    }

    private fun DriveHistory.toUiState(): DriveHistoryDetailUiState {
        return DriveHistoryDetailUiState(
            id = id,
            startLocation = startLocation,
            endLocation = endLocation,
            date = date,
            score = score,
            distance = distance ?: 0,
            duration = duration?.let { minutes ->
                val h = minutes / 60
                val m = minutes % 60
                "${h}H ${m}M"
            } ?: "",
            laneDeviationLeftCount = laneDeviationLeftCount ?: 0,
            laneDeviationRightCount = laneDeviationRightCount ?: 0,
            safeDistanceViolationCount = safeDistanceViolationCount ?: 0,
            suddenDecelerationCount = suddenDecelerationCount ?: 0,
            suddenAccelerationCount = suddenAccelerationCount ?: 0,
            speedingCount = speedingCount ?: 0,
            feedback = feedback ?: emptyList()
        )
    }
}