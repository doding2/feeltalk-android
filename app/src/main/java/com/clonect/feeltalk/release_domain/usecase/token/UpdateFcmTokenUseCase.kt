package com.clonect.feeltalk.release_domain.usecase.token

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class UpdateFcmTokenUseCase(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(fcmToken: String): Resource<Unit> {
        return tokenRepository.updateFcmToken(fcmToken)
    }
}