package com.clonect.feeltalk.new_domain.usecase.token

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository

class GetCachedSocialTokenUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Resource<SocialToken> {
        return tokenRepository.getCachedSocialToken()
    }
}