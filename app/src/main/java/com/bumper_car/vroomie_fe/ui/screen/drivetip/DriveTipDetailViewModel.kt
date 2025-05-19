package com.bumper_car.vroomie_fe.ui.screen.drivetip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.domain.model.DriveTip
import com.bumper_car.vroomie_fe.ui.screen.drivetip.mapper.toDriveTipDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DriveTipDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DriveTipDetailUiState())
    val uiState: StateFlow<DriveTipDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val tips = DriveTip(
                1,
                "차선 변경할 때 알아두면 좋은 것",
                "https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/808a119c2aaa977ce2339679c6096e56.jpg",
                "2025-01-01",
                "초보운전자들이 어려워하는 것 중에 하나가 바로 차선 변경일 것이다. 내 차를 제외하고 모든 차들이 쌩쌩 달리는 도로는 그야말로 정글. 차선 변경을 못해 하염없이 직진만 하는 경우도 꽤 많다.<br/><br/>![Image](https://img.daily.co.kr/@files/www.daily.co.kr/content_watermark/life/2020/20200504/808a119c2aaa977ce2339679c6096e56.jpg)<br/><br/>차선을 변경할 때는 우선 사이드미러를 통해 후방 차량과의 거리를 확인하는 것이 좋다. 사이드미러를 상하로 나눴을 때 후방 차량이 위쪽에 위치한다면 거리가 어느 정도 벌어졌다는 뜻으로, 이때 차선 변경을 시도해야 한다. 차선 변경할 때 가장 중요한 것은 겁을 먹고 속도를 줄이면 안 된다는 점이다. 주행 중 속도를 줄이는 건 사고 위험이 커지기 때문에 속도를 유지하거나 높여야 한다. 아울러 방향지시등을 켜는 것은 기본! 뒤차에게 진로를 알려줘야 안전운전을 진행할 수 있다. 초보운전자들이 어려워하는 것 중에 하나가 바로 차선 변경일 것이다. 내 차를 제외하고 모든 차들이 쌩쌩 달리는 도로는 그야말로 정글. 차선 변경을 못해 하염없이 직진만 하는 경우도 꽤 많다."
            )
            _uiState.value = tips.toDriveTipDetailUiState()
        }
    }
}