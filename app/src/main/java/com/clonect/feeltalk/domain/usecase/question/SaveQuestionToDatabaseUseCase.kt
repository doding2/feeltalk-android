package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.repository.QuestionRepository

class SaveQuestionToDatabaseUseCase(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(question: Question): Resource<Long> {
        return questionRepository.saveQuestionToDatabase(question)
    }
}