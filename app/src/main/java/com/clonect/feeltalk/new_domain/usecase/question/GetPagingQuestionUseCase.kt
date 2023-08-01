package com.clonect.feeltalk.new_domain.usecase.question

import androidx.paging.PagingData
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetPagingQuestionUseCase(
    private val questionRepository: QuestionRepository,
) {
    operator fun invoke(): Flow<PagingData<Question>> {
        return questionRepository.getPagingQuestion()
    }
}