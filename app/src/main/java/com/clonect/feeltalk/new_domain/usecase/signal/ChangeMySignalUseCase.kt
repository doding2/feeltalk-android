package com.clonect.feeltalk.new_domain.usecase.signal

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class ChangeMySignalUseCase(
    private val tokenRepository: TokenRepository,
    private val signalRepository: SignalRepository
) {
    suspend operator fun invoke(signal: Signal): Resource<ChangeMySignalResponse> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return signalRepository.changeMySignal(accessToken, signal)
    }
}