package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.domain.model.user.GoogleTokens
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.UserRepository

class GetGoogleTokensUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(authCode: String): Resource<GoogleTokens> {
        return userRepository.getGoogleTokens(authCode)
    }

}