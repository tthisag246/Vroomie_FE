package com.bumper_car.vroomie_fe.ui.screen.home

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
class HomeViewModel @Inject constructor(
//    private val getDriveScoreUseCase: GetDriveScoreUseCase,
//    private val getSearchHistoryUseCase: GetSearchHistoryUserCase,
//    private val deleteSearchHistoryItemUseCase: DeleteSearchHistoryItemUseCase,
//    private val getDrivingTipsUseCase: GetDrivingTipsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
//            val score = getDriveScoreUseCase()
//            val suggestions = getSearchHistoryUseCase()
//            val tips = getDrivingTipsUseCase()

            _uiState.update {
                it.copy(
//                    driveScore = score,
//                    suggestions = suggestions,
//                    driveInformations = tips
                    driveScore = 63,
                    searchHistory = listOf(
                        "중앙대학교",
                        "강남역",
                        "서울역",
                        "잠실 롯데타워",
                        "노들섬"
                    ),
                    driveInformations = listOf(
                        "오늘의 팁: 브레이크 부드럽게 밟는 법",
                        "셀프 주유하기 도전!",
                        "차 검검은 얼마나 자주 받아야 할까?",
                        "고속도로 주행 안전수칙 5가지"
                    )
                )
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun toggleSearchMode(enable: Boolean) {
        _uiState.update { it.copy(isSearchMode = enable) }
    }

    fun handleSearch(selectedQuery: String) {
        _uiState.update { it.copy(query = selectedQuery, isSearchMode = false) }
    }

    fun updateDriveScore(score: Int) {
        _uiState.update { it.copy(driveScore = score) }
    }

    fun deleteSearchHistoryItem(item: String) {
        _uiState.update { current ->
            current.copy(
                searchHistory = current.searchHistory.filterNot { it == item }
            )
        }
//        viewModelScope.launch {
//            deleteSearchHistoryItemUseCase(item)
//        }
    }

}