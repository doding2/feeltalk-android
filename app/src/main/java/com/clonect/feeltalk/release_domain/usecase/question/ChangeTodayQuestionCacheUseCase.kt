package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository

class ChangeTodayQuestionCacheUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(question: Question?) {
        return questionRepository.changeTodayQuestionCache(question)
    }
}