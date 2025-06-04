package com.bumper_car.vroomie_fe.ui.screen.drive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.model.DriveHistory
import com.bumper_car.vroomie_fe.ui.screen.drive.mapper.toDriveResultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveResultViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DriveResultUiState())
    val uiState: StateFlow<DriveResultUiState> = _uiState.asStateFlow()

    fun setDriveResult(driveResult: DriveHistory) {
        viewModelScope.launch {
            try {
                _uiState.value = driveResult.toDriveResultUiState()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}