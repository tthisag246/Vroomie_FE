package com.bumper_car.vroomie_fe.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.bumper_car.vroomie_fe.domain.usecase.GetDriveScoreUseCase
//import com.bumper_car.vroomie_fe.domain.usecase.GetDrivingTipsUseCase
//import com.bumper_car.vroomie_fe.domain.usecase.GetSearchHistoryUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    nickname = "abc@gmail.com",
                    carModel = "K7",
                    carType = "승용차",
                    hasHiPass = true,
                    fuelType = "휘발유",
                    level = 3
                )
            }
        }
    }
}