package com.bumper_car.vroomie_fe.ui.screen.drive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultVideoItem
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import com.bumper_car.vroomie_fe.domain.usecase.SaveDriveResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NaviViewModel @Inject constructor(
    private val saveDriveResultUseCase: SaveDriveResultUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DriveResultUiState())
    val uiState: StateFlow<DriveResultUiState> = _uiState.asStateFlow()

    fun setStartAt(time: String) {
        _uiState.update { it.copy(startAt = time) }
    }

    fun setEndAt(time: String) {
        _uiState.update { it.copy(endAt = time) }
    }

    fun setStartLocation(location: String) {
        _uiState.update { it.copy(startLocation = location) }
    }

    fun setEndLocation(location: String) {
        _uiState.update { it.copy(endLocation = location) }
    }

    fun updateDistanceAndDuration(distanceMeters: Float, durationSeconds: Int) {
        _uiState.update { it.copy(distance = distanceMeters, duration = durationSeconds) }
    }

    fun incrementLaneDeviationLeftCount() {
        _uiState.update { it.copy(laneDeviationLeftCount = it.laneDeviationLeftCount + 1) }
    }

    fun incrementLaneDeviationRightCount() {
        _uiState.update { it.copy(laneDeviationRightCount = it.laneDeviationRightCount + 1) }
    }

    fun incrementSafeDistanceViolationCount() {
        _uiState.update { it.copy(safeDistanceViolationCount = it.safeDistanceViolationCount + 1) }
    }

    fun incrementSuddenDecelerationCount() {
        _uiState.update { it.copy(suddenDecelerationCount = it.suddenDecelerationCount + 1) }
    }

    fun incrementSuddenAccelerationCount() {
        _uiState.update { it.copy(suddenAccelerationCount = it.suddenAccelerationCount + 1) }
    }

    fun saveDriveResult(
        onSuccess: (DriveHistory) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentUiState = _uiState.value
                val response = saveDriveResultUseCase(
                    DriveResultRequest(
                        startAt = currentUiState.startAt,
                        endAt = currentUiState.endAt,
                        startLocation = currentUiState.startLocation,
                        endLocation = currentUiState.endLocation,
                        distance = currentUiState.distance,
                        duration = currentUiState.duration,
                        laneDeviationLeftCount = currentUiState.laneDeviationLeftCount,
                        laneDeviationRightCount = currentUiState.laneDeviationRightCount,
                        safeDistanceViolationCount = currentUiState.safeDistanceViolationCount,
                        suddenDecelerationCount = currentUiState.suddenDecelerationCount,
                        suddenAccelerationCount = currentUiState.suddenAccelerationCount,
                        speedingCount = _uiState.value.speedingCount,
                        videos = _uiState.value.feedback.map { videoItem ->
                            DriveResultVideoItem(
                                videoId = 0,
                                title = videoItem.title,
                                content = videoItem.content,
                                url = videoItem.videoUrl
                            )
                        }
                    )
                )
                onSuccess(response)
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }
    }

    suspend fun saveDriveResultDirect(): DriveHistory {
        val currentUiState = _uiState.value
        return saveDriveResultUseCase(
            DriveResultRequest(
                startAt = currentUiState.startAt,
                endAt = currentUiState.endAt,
                startLocation = currentUiState.startLocation,
                endLocation = currentUiState.endLocation,
                distance = currentUiState.distance,
                duration = currentUiState.duration,
                laneDeviationLeftCount = currentUiState.laneDeviationLeftCount,
                laneDeviationRightCount = currentUiState.laneDeviationRightCount,
                safeDistanceViolationCount = currentUiState.safeDistanceViolationCount,
                suddenDecelerationCount = currentUiState.suddenDecelerationCount,
                suddenAccelerationCount = currentUiState.suddenAccelerationCount,
                speedingCount = currentUiState.speedingCount,
                videos = _uiState.value.feedback.map { videoItem ->
                    DriveResultVideoItem(
                        videoId = 0,
                        title = videoItem.title,
                        content = videoItem.content,
                        url = videoItem.videoUrl
                    )
                }
            )
        )
    }
}