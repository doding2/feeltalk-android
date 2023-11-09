package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository

class ModifyPartnerChallengeCacheUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(challenge: Challenge) {
        challengeRepository.modifyPartnerChallengeCache(challenge)
    }
}