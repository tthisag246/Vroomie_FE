package com.bumper_car.vroomie_fe.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.ui.screen.mypage.mapper.toMyPageUiState
import com.bumper_car.vroomie_fe.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    fun fetchMyInfo() {
        viewModelScope.launch {
            try {
                val myInfo = getUserInfoUseCase()
                val myInfoItem = myInfo.toMyPageUiState()
                _uiState.value = myInfoItem
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}