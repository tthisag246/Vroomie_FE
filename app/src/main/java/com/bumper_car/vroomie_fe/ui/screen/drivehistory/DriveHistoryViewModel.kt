package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DriveHistoryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DriveHistoryUiState())
    val uiState: StateFlow<DriveHistoryUiState> = _uiState.asStateFlow()

}