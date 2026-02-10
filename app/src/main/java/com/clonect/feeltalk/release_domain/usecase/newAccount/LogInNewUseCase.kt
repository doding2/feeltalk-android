package com.clonect.feeltalk.release_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.account.SocialType
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class LogInNewUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(oauthId: String, snsType: SocialType): Resource<Unit> {
        return when (val result = accountRepository.logInNew(oauthId, snsType)) {
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