package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.repository.QuestionRepository2

class SaveQuestionToDatabaseUseCase(
    private val questionRepository2: QuestionRepository2
) {
    suspend operator fun invoke(question2: Question2): Resource<Long> {
        return questionRepository2.saveQuestionToDatabase(question2)
    }
}