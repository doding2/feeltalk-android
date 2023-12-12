package com.clonect.feeltalk.new_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class LogInAppleUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(state: String): Resource<Unit> {
        return when (val result = accountRepository.logInApple(state)) {
            is Resource.Success -> {
                tokenRepository.saveTokenInfo(result.data)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(result.throwable)
            }
        }
    }
}