package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository

class AnswerPartnerQuestionCacheUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(question: Question) {
        questionRepository.answerPartnerQuestionCache(question)
    }
}