package com.bumper_car.vroomie_fe.ui.screen.drive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultRequest
import com.bumper_car.vroomie_fe.data.remote.drive.DriveResultVideoItem
import com.bumper_car.vroomie_fe.domain.usecase.SaveDriveResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NaviViewModel @Inject constructor(
    private val saveDriveResultUseCase: SaveDriveResultUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DriveResultUiState())
    val uiState: StateFlow<DriveResultUiState> = _uiState.asStateFlow()

    fun saveDriveResultAndNavigate(
        navController: NavHostController,
        driveResultViewModel: DriveResultViewModel
    ) {
        viewModelScope.launch {
            try {
                val response = saveDriveResultUseCase(
                    DriveResultRequest(
                        startAt = _uiState.value.startAt,
                        endAt = _uiState.value.endAt,
                        startLocation = _uiState.value.startLocation,
                        endLocation = _uiState.value.endLocation,
                        distance = _uiState.value.distance,
                        duration = _uiState.value.duration,
                        laneDeviationLeftCount = _uiState.value.laneDeviationLeftCount,
                        laneDeviationRightCount = _uiState.value.laneDeviationRightCount,
                        safeDistanceViolationCount = _uiState.value.safeDistanceViolationCount,
                        suddenDecelerationCount = _uiState.value.suddenDecelerationCount,
                        suddenAccelerationCount = _uiState.value.suddenAccelerationCount,
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

                driveResultViewModel.setDriveResult(response)
                navController.navigate("drive/result")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}