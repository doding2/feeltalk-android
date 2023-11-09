package com.clonect.feeltalk.new_domain.usecase.question

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetAnswerQuestionFlowUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(): Flow<Question> {
        return questionRepository.getAnswerQuestionFlow()
    }
}