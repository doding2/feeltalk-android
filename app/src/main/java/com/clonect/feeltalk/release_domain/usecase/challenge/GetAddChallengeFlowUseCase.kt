package com.clonect.feeltalk.release_domain.usecase.challenge

import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow

class GetAddChallengeFlowUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(): Flow<Challenge> {
        return challengeRepository.getAddChallengeFlow()
    }
}