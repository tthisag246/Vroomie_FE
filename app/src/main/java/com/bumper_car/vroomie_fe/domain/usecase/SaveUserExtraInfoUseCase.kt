package com.bumper_car.vroomie_fe.domain.usecase

import com.bumper_car.vroomie_fe.data.remote.user.SignUpExtraInfoRequest
import com.bumper_car.vroomie_fe.data.repository.UserRepository
import jakarta.inject.Inject

class SaveUserExtraInfoUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(signUpExtraInfoRequest: SignUpExtraInfoRequest): Boolean {
        return repository.saveUserInfo(signUpExtraInfoRequest)
    }
}
