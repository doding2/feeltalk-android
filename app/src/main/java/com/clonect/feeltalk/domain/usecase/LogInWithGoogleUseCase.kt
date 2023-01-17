package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.domain.model.user.LogInGoogleResponse
import com.clonect.feeltalk.data.util.Result
import com.clonect.feeltalk.domain.repository.UserRepository

class LogInWithGoogleUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(authCode: String): Result<LogInGoogleResponse> {
        return userRepository.fetchGoogleAuthInfo(authCode)
    }

}