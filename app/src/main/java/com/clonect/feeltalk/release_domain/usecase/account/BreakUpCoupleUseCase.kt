package com.clonect.feeltalk.release_domain.usecase.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class BreakUpCoupleUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val tokenInfoData = (tokenInfo as Resource.Success).data
        val accessToken = tokenInfoData.accessToken
        val breakUpResult = accountRepository.breakUpCouple(accessToken)
        tokenRepository.saveTokenInfo(tokenInfoData)
        return breakUpResult
    }
}