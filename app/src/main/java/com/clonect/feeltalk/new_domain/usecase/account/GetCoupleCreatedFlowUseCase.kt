package com.clonect.feeltalk.new_domain.usecase.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import kotlinx.coroutines.flow.Flow

class GetCoupleCreatedFlowUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return accountRepository.getCoupleCreatedFlow()
    }
}