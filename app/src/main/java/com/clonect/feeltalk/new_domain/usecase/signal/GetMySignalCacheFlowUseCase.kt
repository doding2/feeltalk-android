package com.clonect.feeltalk.new_domain.usecase.signal

import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.repository.signal.SignalRepository
import kotlinx.coroutines.flow.Flow

class GetMySignalCacheFlowUseCase(
    private val signalRepository: SignalRepository
) {
    suspend operator fun invoke(): Flow<Signal?> {
        return signalRepository.getMySignalCacheFlow()
    }
}