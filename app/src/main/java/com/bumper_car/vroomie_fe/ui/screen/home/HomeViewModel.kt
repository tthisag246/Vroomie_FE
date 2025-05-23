package com.bumper_car.vroomie_fe.ui.screen.home

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.data.remote.kakao.KakaoLocalApiService
import com.bumper_car.vroomie_fe.data.remote.kakao.model.AddressDocument
import com.bumper_car.vroomie_fe.ui.screen.drive.NaviActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val kakaoLocalApiService: KakaoLocalApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    driveScore = 63,
                    searchHistory = listOf(
                        "중앙대학교", "강남역", "서울역", "잠실 롯데타워", "노들섬"
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
        viewModelScope.launch {
            try {
                val response = kakaoLocalApiService.getAddressFromQuery(selectedQuery)
                response.documents.firstOrNull()?.let { document ->
                    _uiState.update {
                        it.copy(
                            query = selectedQuery,
                            isSearchMode = false,
                            navigationLat = document.y,
                            navigationLng = document.x,
                            navigationPlaceName = document.address_name
                        )
                    }
                } ?: run {
                    // 검색 결과 없음 처리
                }
            } catch (e: Exception) {
                // 네트워크 에러 등 처리
            }
        }
    }

    fun updateDriveScore(score: Int) {
        _uiState.update { it.copy(driveScore = score) }
    }

    fun deleteSearchHistoryItem(item: String) {
        _uiState.update {
            it.copy(searchHistory = it.searchHistory.filterNot { it == item })
        }
    }

    fun geocode(address: String, onResult: (AddressDocument?) -> Unit) {
        viewModelScope.launch {
            try {
                // 로그: API 요청 시작
                Log.d("NaviDebug", "📡 geocode() 호출됨 - address: $address")

                val response = kakaoLocalApiService.getAddressFromQuery(address)

                val document = response.documents.firstOrNull()

                if (document != null) {
                    Log.d("NaviDebug", "✅ 주소 변환 성공: ${document.address_name}, (${document.y}, ${document.x})")
                } else {
                    Log.w("NaviDebug", "⚠️ 주소 결과 없음")
                }

                onResult(document)

            } catch (e: Exception) {
                Log.e("NaviDebug", "❌ 주소 변환 실패 - ${e.localizedMessage}", e)
                onResult(null)
            }
        }
    }

    fun addSearchHistory(query: String) {
        _uiState.update {
            if (query.isNotBlank() && !it.searchHistory.contains(query)) {
                it.copy(searchHistory = listOf(query) + it.searchHistory)
            } else {
                it
            }
        }
    }
}
