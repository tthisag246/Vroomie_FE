package com.bumper_car.vroomie_fe.ui.screen.drivetip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.model.DriveTip
import com.bumper_car.vroomie_fe.domain.usecase.GetDriveTipUseCase
import com.bumper_car.vroomie_fe.ui.screen.drivehistory.mapper.toDriveHistoryDetailUiState
import com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper.toDriveTipDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveTipDetailViewModel @Inject constructor(
    private val getDriveTipUseCase: GetDriveTipUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriveTipDetailUiState())
    val uiState: StateFlow<DriveTipDetailUiState> = _uiState.asStateFlow()

    fun fetchDriveTip(tipId: Int) {
        viewModelScope.launch {
            try {
                val tip = getDriveTipUseCase(tipId)
                val tipItem = tip.toDriveTipDetailUiState()
                _uiState.value = tipItem
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}