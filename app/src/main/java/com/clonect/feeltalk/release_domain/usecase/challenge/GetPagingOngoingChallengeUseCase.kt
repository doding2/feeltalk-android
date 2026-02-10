package com.clonect.feeltalk.release_domain.usecase.challenge

import androidx.paging.PagingData
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow

class GetPagingOngoingChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    operator fun invoke(): Flow<PagingData<Challenge>> {
        return challengeRepository.getPagingOngoingChallenge()
    }
}