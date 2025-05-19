package com.bumper_car.vroomie_fe.ui.screen.drivetip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.model.DriveTip
import com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper.toDriveTipItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveTipViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DriveTipUiState())
    val uiState: StateFlow<DriveTipUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val tips = listOf(
                DriveTip(1, "차선 변경할 때 알아두면 좋은 것", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/808a119c2aaa977ce2339679c6096e56.jpg", "2025-01-01"),
                DriveTip(2, "오르막길 내리막길 누가 우선일까?", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/2de1663148b4d7c3b0c95cebf6b8160b.jpg", "2025-01-01"),
                DriveTip(3, "도로 위 마름모, 뭘 의미하는 걸까?", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/1dcf6b3dc5cad71ded9c9c72324c740e.jpg", "2025-01-01"),
                DriveTip(4, "비보호 좌회전은 어떻게?", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/cb243d6fcfa91e3a850c1039f650e6c7.jpg", "2025-01-01"),
                DriveTip(5, "녹색 신호일 때 횡단보도에 보행자 없으면 우회전해도 될까?", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/6adc08d70ca8492e838e6d6c9348935e.jpg", "2025-01-01"),
                DriveTip(6, "회전교차로 어떻게 빠져나올까?", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/a6dc5966dbaa292a8bf901a340d3da68.jpg", "2025-01-01"),
                DriveTip(1, "차선 변경할 때 알아두면 좋은 것", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/808a119c2aaa977ce2339679c6096e56.jpg", "2025-01-01"),
                DriveTip(2, "오르막길 내리막길 누가 우선일까?", "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/2de1663148b4d7c3b0c95cebf6b8160b.jpg", "2025-01-01")
            )
            _uiState.value = DriveTipUiState(
                tipList = tips.map { it.toDriveTipItemUiState()}
            )
        }
    }
}