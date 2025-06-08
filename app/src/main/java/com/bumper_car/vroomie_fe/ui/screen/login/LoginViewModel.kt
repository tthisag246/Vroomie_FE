package com.bumper_car.vroomie_fe.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.data.local.TokenPreferences
import com.bumper_car.vroomie_fe.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenPreferences: TokenPreferences,
    private val userRepository: UserRepository
) : ViewModel() {
    fun saveToken(token: String) = viewModelScope.launch {
        tokenPreferences.setToken(token)
    }

    suspend fun checkIfExtraInfoExists(): Boolean {
        return try {
            val user = userRepository.getUserInfo()

            user.userName?.isNotBlank() == true
        } catch (e: Exception) {
            println("사용자 정보 조회 중 오류 발생: ${e.localizedMessage}")
            false
        }
    }
}
