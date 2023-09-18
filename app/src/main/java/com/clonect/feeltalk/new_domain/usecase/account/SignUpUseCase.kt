package com.clonect.feeltalk.new_domain.usecase.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class SignUpUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): Resource<TokenInfo> {
        val socialTokenResult = tokenRepository.getCachedSocialToken()
        if (socialTokenResult is Resource.Error) {
            return socialTokenResult
        }
        val socialToken = (socialTokenResult as Resource.Success).data

        val signUpResult = accountRepository.signUp(socialToken, isMarketingConsentAgreed, nickname, fcmToken)
        if (signUpResult is Resource.Success) {
            tokenRepository.saveTokenInfo(signUpResult.data)
        }
        return signUpResult
    }
}