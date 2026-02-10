package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetTodayQuestionFlowUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(): Flow<Question?> {
        return questionRepository.getTodayQuestionFlow()
    }
}