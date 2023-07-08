package com.clonect.feeltalk.new_domain.usecase.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository

class ReLogInUseCase(
    private val tokenRepository: TokenRepository,
    private val signInRepository: SignInRepository,
) {
    suspend operator fun invoke(socialToken: SocialToken): Resource<String> {
        return when (val result = signInRepository.reLogIn(socialToken)) {
            is Resource.Success -> {
                val (signUpState, tokenInfo) = result.data
                if (tokenInfo != null)
                    tokenRepository.saveTokenInfo(tokenInfo)
                Resource.Success(signUpState)
            }
            is Resource.Error -> {
                Resource.Error(result.throwable)
            }
        }
    }
}