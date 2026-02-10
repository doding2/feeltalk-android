package com.clonect.feeltalk.release_domain.usecase.signal

import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.repository.signal.SignalRepository
import kotlinx.coroutines.flow.Flow

class GetMySignalCacheFlowUseCase(
    private val signalRepository: SignalRepository
) {
    suspend operator fun invoke(): Flow<Signal?> {
        return signalRepository.getMySignalCacheFlow()
    }
}