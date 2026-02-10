package com.clonect.feeltalk.release_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository

class RetryRequestAdultAuthCodeUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        sessionUuid: String
    ): Resource<Unit> {
        return accountRepository.retryRequestAdultAuthCode(sessionUuid)
    }
}