package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.usecase.GetDriveHistoryUseCase
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.mapper.toDriveHistoryDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveHistoryDetailViewModel @Inject constructor(
    private val getDriveHistoryUseCase: GetDriveHistoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DriveHistoryDetailUiState())
    val uiState: StateFlow<DriveHistoryDetailUiState> = _uiState.asStateFlow()

    fun fetchDriveHistory(historyId: Int) {
        viewModelScope.launch {
            try {
                val history = getDriveHistoryUseCase(historyId)
                val historyItem = history.toDriveHistoryDetailUiState()
                _uiState.value = historyItem
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}