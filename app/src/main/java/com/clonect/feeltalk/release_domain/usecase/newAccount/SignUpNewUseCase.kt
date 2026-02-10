package com.clonect.feeltalk.release_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class SignUpNewUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        nickname: String,
        marketingConsent: Boolean,
        fcmToken: String,
        appleState: String? = null,
    ): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return accountRepository.signUpNew(accessToken, nickname, marketingConsent, fcmToken, appleState)
    }
}