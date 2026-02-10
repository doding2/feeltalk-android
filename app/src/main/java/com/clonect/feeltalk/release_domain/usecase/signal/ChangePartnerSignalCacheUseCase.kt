package com.clonect.feeltalk.release_domain.usecase.signal

import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.repository.signal.SignalRepository

class ChangePartnerSignalCacheUseCase(
    private val signalRepository: SignalRepository
) {
    suspend operator fun invoke(signal: Signal) {
        return signalRepository.changePartnerSignalCache(signal)
    }
}