package com.clonect.feeltalk.release_domain.usecase.question

import androidx.paging.PagingData
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetPagingQuestionUseCase(
    private val questionRepository: QuestionRepository,
) {
    operator fun invoke(): Flow<PagingData<Question>> {
        return questionRepository.getPagingQuestion()
    }
}