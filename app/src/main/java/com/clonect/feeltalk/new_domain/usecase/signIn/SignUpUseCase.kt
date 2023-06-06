package com.clonect.feeltalk.new_domain.usecase.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository

class SignUpUseCase(
    private val tokenRepository: TokenRepository,
    private val signInRepository: SignInRepository
) {
    suspend operator fun invoke(nickname: String): Resource<TokenInfo> {
        val socialTokenResult = tokenRepository.getCachedSocialToken()
        if (socialTokenResult is Resource.Error) {
            return socialTokenResult
        }
        val socialToken = (socialTokenResult as Resource.Success).data

        val signUpResult = signInRepository.signUp(socialToken, nickname)
        if (signUpResult is Resource.Success) {
            tokenRepository.saveTokenInfo(signUpResult.data)
        }
        return signUpResult
    }
}