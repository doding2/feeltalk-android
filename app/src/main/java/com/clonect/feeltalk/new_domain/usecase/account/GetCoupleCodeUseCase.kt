package com.clonect.feeltalk.new_domain.usecase.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class GetCoupleCodeUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Resource<CoupleCodeDto> {
        val accessTokenResult = tokenRepository.getTokenInfo()
        if (accessTokenResult is Resource.Error) {
            return accessTokenResult
        }
        val tokenInfo = (accessTokenResult as Resource.Success).data
        return accountRepository.getCoupleCode(tokenInfo.accessToken)
    }
}