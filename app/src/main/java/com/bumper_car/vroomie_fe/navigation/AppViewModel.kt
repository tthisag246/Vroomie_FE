package com.bumper_car.vroomie_fe.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.data.local.TokenPreferences
import com.bumper_car.vroomie_fe.domain.usecase.GetMyInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenPreferences: TokenPreferences,
    private val getMyInfoUseCase: GetMyInfoUseCase
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    init {
        viewModelScope.launch {
            val token = tokenPreferences.tokenFlow.first()
            Log.d("TokenLog", token.toString())
            if (token == null) {
                _isLoggedIn.value = null
            } else if (token.isEmpty()) {
                _isLoggedIn.value = false
            } else {
                try {
                    getMyInfoUseCase()
                    _isLoggedIn.value = true
                } catch (e: Exception) {
                    tokenPreferences.clearToken()
                    _isLoggedIn.value = false
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenPreferences.clearToken()
            _isLoggedIn.value = false
        }
    }
}