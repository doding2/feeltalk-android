package com.clonect.feeltalk.new_domain.usecase.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.signIn.CoupleCodeDto
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class GetCoupleCodeUseCase(
    private val tokenRepository: TokenRepository,
    private val signInRepository: SignInRepository
) {
    suspend operator fun invoke(): Resource<CoupleCodeDto> {
        val accessTokenResult = tokenRepository.getTokenInfo()
        if (accessTokenResult is Resource.Error) {
            return accessTokenResult
        }
        val tokenInfo = (accessTokenResult as Resource.Success).data
        return signInRepository.getCoupleCode(tokenInfo.accessToken)
    }
}