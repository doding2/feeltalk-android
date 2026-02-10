package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetQuestionUpdatedFlowUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return questionRepository.getQuestionUpdatedFlow()
    }
}