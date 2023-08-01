package com.clonect.feeltalk.new_domain.usecase.question

import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository

class ChangeTodayQuestionCacheUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(question: Question) {
        return questionRepository.changeTodayQuestionCache(question)
    }
}