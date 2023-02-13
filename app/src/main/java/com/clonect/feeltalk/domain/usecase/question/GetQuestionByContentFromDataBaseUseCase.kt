package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.repository.QuestionRepository

/* Only For Fcm Service */
class GetQuestionByContentFromDataBaseUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(question: String): Resource<Question> {
        return questionRepository.getQuestionByContentFromDB(question)
    }
}