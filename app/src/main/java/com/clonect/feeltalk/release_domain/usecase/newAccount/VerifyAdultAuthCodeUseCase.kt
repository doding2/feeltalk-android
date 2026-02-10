package com.clonect.feeltalk.release_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository

class VerifyAdultAuthCodeUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        authNumber: String,
        sessionUuid: String
    ): Resource<Unit> {
        return accountRepository.verifyAdultAuthCode(authNumber, sessionUuid)
    }
}