package com.clonect.feeltalk.new_domain.usecase.signal

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import kotlinx.coroutines.flow.Flow

class GetPartnerSignalFlowUseCase(
    private val signalRepository: SignalRepository
) {
    suspend operator fun invoke(): Flow<Signal?> {
        return signalRepository.getPartnerSignalFlow()
    }
}