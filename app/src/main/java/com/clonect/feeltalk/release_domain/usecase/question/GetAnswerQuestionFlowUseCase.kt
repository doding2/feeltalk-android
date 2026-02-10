package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetAnswerQuestionFlowUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(): Flow<Question> {
        return questionRepository.getAnswerQuestionFlow()
    }
}