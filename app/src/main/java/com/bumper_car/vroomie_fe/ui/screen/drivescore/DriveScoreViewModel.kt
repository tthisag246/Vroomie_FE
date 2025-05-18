package com.bumper_car.vroomie_fe.ui.screen.drivescore

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
class DriveScoreViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DriveScoreUiState())
    val uiState: StateFlow<DriveScoreUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    driveScore = 75,
                    rankPercent = 58,
                    months = listOf("9월", "10월", "11월", "12월", "1월", "2월", "3월", "4월", "5월"),
                    scores = listOf(56, 64, 67, 73, 82, 83, 84, 73, 75),
                    selectedIndex = 8,
                    driveStatsList = listOf(
                        DriveStats("123km", "4시간", "200m", "1회", "2회", "0회", "0회"),
                        DriveStats("150km", "5시간", "100m", "0회", "1회", "0회", "0회"),
                        DriveStats("110km", "4시간", "80m", "0회", "0회", "0회", "0회"),
                        DriveStats("90km",  "3시간", "150m", "2회", "0회", "0회", "0회"),
                        DriveStats("70km", "2시간", "400m", "1회", "1회", "0회", "0회"),
                        DriveStats("123.7km", "4시간 18분", "479m", "0회", "0회", "0회", "0회"),
                        DriveStats("253.2km", "6시간 2분", "1.5km", "0회", "0회", "0회", "0회"),
                        DriveStats("200km", "5시간 20분", "1.3km", "0회", "0회", "0회", "0회"),
                        DriveStats("325.2km", "8시간 36분", "2.7km", "3회", "0회", "0회", "0회")
                    )
                )
            }
        }
    }

    fun updateSelectedIndex(index: Int) {
        _uiState.update { it.copy(selectedIndex = index) }
    }

}