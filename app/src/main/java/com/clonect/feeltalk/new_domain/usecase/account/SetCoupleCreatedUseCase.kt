package com.clonect.feeltalk.new_domain.usecase.account

import com.clonect.feeltalk.new_domain.repository.account.AccountRepository

class SetCoupleCreatedUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(isCreated: Boolean) {
        return accountRepository.setCoupleCreated(isCreated)
    }
}