package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetChallengeUpdatedFlowUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return challengeRepository.getChallengeUpdatedFlow()
    }
}