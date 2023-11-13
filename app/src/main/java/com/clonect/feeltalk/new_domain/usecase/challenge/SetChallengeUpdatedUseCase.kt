package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository

class SetChallengeUpdatedUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(isUpdated: Boolean): Resource<Unit> {
        return challengeRepository.setChallengeUpdated(isUpdated)
    }
}