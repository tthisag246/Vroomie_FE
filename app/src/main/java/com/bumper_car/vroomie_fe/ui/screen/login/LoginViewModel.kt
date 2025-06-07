package com.bumper_car.vroomie_fe.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.data.local.TokenPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenPreferences: TokenPreferences
) : ViewModel() {
    fun saveToken(token: String) = viewModelScope.launch {
        tokenPreferences.setToken(token)
    }
}
