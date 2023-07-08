package com.clonect.feeltalk.new_domain.usecase.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.signIn.AutoLogInDto
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository

class AutoLogInUseCase(
    private val tokenRepository: TokenRepository,
    private val signInRepository: SignInRepository,
) {
    suspend operator fun invoke(): Resource<AutoLogInDto> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return signInRepository.autoLogIn(accessToken)
    }
}