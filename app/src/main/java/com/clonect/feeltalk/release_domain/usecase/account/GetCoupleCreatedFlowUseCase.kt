package com.clonect.feeltalk.release_domain.usecase.account

import com.clonect.feeltalk.release_domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetCoupleCreatedFlowUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return accountRepository.getCoupleCreatedFlow()
    }
}