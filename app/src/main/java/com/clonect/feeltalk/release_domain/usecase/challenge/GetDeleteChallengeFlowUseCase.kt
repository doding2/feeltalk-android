package com.clonect.feeltalk.release_domain.usecase.challenge

import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow

class GetDeleteChallengeFlowUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(): Flow<Challenge> {
        return challengeRepository.getDeleteChallengeFlow()
    }
}