package com.bumper_car.vroomie_fe.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumper_car.vroomie_fe.data.remote.user.SignUpExtraInfoRequest
import com.bumper_car.vroomie_fe.domain.usecase.SaveUserExtraInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class SignUpExtraInfoViewModel @Inject constructor(
    private val saveUserExtraInfoUseCase: SaveUserExtraInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpExtraInfoUiState())
    val uiState: StateFlow<SignUpExtraInfoUiState> = _uiState.asStateFlow()

    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }

    fun updateCarModel(model: String) {
        _uiState.value = _uiState.value.copy(carModel = model)
    }

    fun updateCarHipass(hipass: Boolean) {
        _uiState.value = _uiState.value.copy(carHipass = hipass)
    }

    fun updateCarType(type: CarTypeEnum) {
        _uiState.value = _uiState.value.copy(carType = type)
    }

    fun updateCarFuel(fuel: FuelTypeEnum) {
        _uiState.value = _uiState.value.copy(carFuel = fuel)
    }

    fun registerExtraInfo(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                saveUserExtraInfoUseCase(
                    SignUpExtraInfoRequest(
                        user_name = _uiState.value.userName,
                        car_model = _uiState.value.carModel,
                        car_hipass = _uiState.value.carHipass,
                        car_type = _uiState.value.carType?.name,
                        car_fuel = _uiState.value.carFuel?.name
                    )
                )
                val prefs = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
                prefs.edit().putString("username", _uiState.value.userName).apply()

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
