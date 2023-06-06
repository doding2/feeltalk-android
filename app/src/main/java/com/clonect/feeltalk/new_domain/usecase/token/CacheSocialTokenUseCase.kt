package com.clonect.feeltalk.new_domain.usecase.token

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository

class CacheSocialTokenUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(socialToken: SocialToken): Resource<Unit> {
        return tokenRepository.cacheSocialToken(socialToken)
    }
}