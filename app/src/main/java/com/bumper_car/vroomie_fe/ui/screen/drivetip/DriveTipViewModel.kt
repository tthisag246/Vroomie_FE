package com.bumper_car.vroomie_fe.ui.screen.drivetip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.usecase.GetDriveTipsUseCase
import com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper.toDriveTipItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveTipViewModel @Inject constructor(
    private val getDriveTipsUseCase: GetDriveTipsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriveTipUiState())
    val uiState: StateFlow<DriveTipUiState> = _uiState.asStateFlow()

    fun fetchDriveTips() {
        viewModelScope.launch {
            try {
                val tips = getDriveTipsUseCase()
                val tipItems = tips.map { it.toDriveTipItemUiState() }
                _uiState.value = DriveTipUiState(tips = tipItems)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}