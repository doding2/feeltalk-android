package com.clonect.feeltalk.release_domain.usecase.challenge

import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow

class GetChallengeUpdatedFlowUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return challengeRepository.getChallengeUpdatedFlow()
    }
}