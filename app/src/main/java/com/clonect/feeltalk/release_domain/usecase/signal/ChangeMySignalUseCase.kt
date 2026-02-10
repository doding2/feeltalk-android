package com.clonect.feeltalk.release_domain.usecase.signal

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.release_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class ChangeMySignalUseCase(
    private val tokenRepository: TokenRepository,
    private val signalRepository: SignalRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(signal: Signal): Resource<ChangeMySignalResponse> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = signalRepository.changeMySignal(accessToken, signal)

        if (result is Resource.Success) {
            mixpanelRepository.changeMySignal()
        }

        return result
    }
}