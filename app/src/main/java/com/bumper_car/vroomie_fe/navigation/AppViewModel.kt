package com.bumper_car.vroomie_fe.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    var isLoggedIn by mutableStateOf<Boolean>(false)
        private set

    init {
        viewModelScope.launch {
            checkLoginStatus()
        }
    }

    fun checkLoginStatus() {
        /* TODO: 로그인 확인 usecase (DataStore에 토큰 있는지 확인) */
//        isLoggedIn = true
    }


    fun logout() {
        viewModelScope.launch {
            /* TODO: 로그아웃 usecase */
            isLoggedIn = false
        }
    }
}