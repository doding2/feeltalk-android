package com.clonect.feeltalk.release_domain.usecase.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.token.SocialToken
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class ReLogInUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(socialToken: SocialToken): Resource<String> {
        return when (val result = accountRepository.reLogIn(socialToken)) {
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