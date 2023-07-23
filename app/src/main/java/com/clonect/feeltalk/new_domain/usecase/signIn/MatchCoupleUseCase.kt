package com.clonect.feeltalk.new_domain.usecase.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class MatchCoupleUseCase(
    private val tokenRepository: TokenRepository,
    private val signInRepository: SignInRepository
) {
    suspend operator fun invoke(coupleCode: String): Resource<Unit> {
        val accessTokenResult = tokenRepository.getTokenInfo()
        if (accessTokenResult is Resource.Error) {
            return accessTokenResult
        }
        val tokenInfo = (accessTokenResult as Resource.Success).data
        return signInRepository.matchCouple(tokenInfo.accessToken, coupleCode)
    }
}