package com.clonect.feeltalk.release_domain.usecase.signal

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class GetPartnerSignalUseCase(
    private val tokenRepository: TokenRepository,
    private val signalRepository: SignalRepository
) {
    suspend operator fun invoke(): Resource<Signal> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return signalRepository.getPartnerSignal(accessToken)
    }
}