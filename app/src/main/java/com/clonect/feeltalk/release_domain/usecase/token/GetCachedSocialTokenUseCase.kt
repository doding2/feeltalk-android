package com.clonect.feeltalk.release_domain.usecase.token

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.token.SocialToken
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class GetCachedSocialTokenUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Resource<SocialToken> {
        return tokenRepository.getCachedSocialToken()
    }
}