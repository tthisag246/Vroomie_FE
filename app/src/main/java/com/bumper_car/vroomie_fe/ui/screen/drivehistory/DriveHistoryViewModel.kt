package com.bumper_car.vroomie_fe.ui.screen.drivehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.usecase.GetDriveHistoriesUseCase
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.mapper.toDriveHistoryItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveHistoryViewModel @Inject constructor(
    private val getDriveHistoriesUseCase: GetDriveHistoriesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DriveHistoryUiState())
    val uiState: StateFlow<DriveHistoryUiState> = _uiState.asStateFlow()

    fun fetchDriveHistories() {
        viewModelScope.launch {
            try {
                val histories = getDriveHistoriesUseCase()
                val historyItems = histories.map { it.toDriveHistoryItemUiState() }
                _uiState.value = _uiState.value.copy(histories = historyItems)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}