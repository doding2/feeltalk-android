package com.clonect.feeltalk.new_domain.usecase.challenge

import androidx.paging.PagingData
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow

class GetPagingCompletedChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    operator fun invoke(): Flow<PagingData<Challenge>> {
        return challengeRepository.getPagingCompletedChallenge()
    }
}