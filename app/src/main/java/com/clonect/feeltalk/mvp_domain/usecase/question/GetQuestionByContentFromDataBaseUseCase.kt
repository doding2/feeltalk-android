package com.clonect.feeltalk.mvp_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import com.clonect.feeltalk.mvp_domain.repository.QuestionRepository2

/* Only For Fcm Service */
class GetQuestionByContentFromDataBaseUseCase(
    private val questionRepository2: QuestionRepository2,
) {
    suspend operator fun invoke(question: String): Resource<Question2> {
        return questionRepository2.getQuestionByContentFromDB(question)
    }
}