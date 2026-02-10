package com.clonect.feeltalk.release_domain.usecase.account

import com.clonect.feeltalk.release_domain.repository.account.AccountRepository

class SetCoupleCreatedUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(isCreated: Boolean) {
        return accountRepository.setCoupleCreated(isCreated)
    }
}