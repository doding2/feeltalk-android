package com.clonect.feeltalk.new_domain.usecase.question

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository

class AnswerPartnerQuestionCacheUseCase(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(question: Question) {
        questionRepository.answerPartnerQuestionCache(question)
    }
}