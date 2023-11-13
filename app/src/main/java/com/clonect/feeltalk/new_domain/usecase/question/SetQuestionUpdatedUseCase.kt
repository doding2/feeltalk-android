package com.clonect.feeltalk.new_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository

class SetQuestionUpdatedUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(isUpdated: Boolean): Resource<Unit> {
        return questionRepository.setQuestionUpdated(isUpdated)
    }
}