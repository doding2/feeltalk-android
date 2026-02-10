package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class AnswerQuestionUseCase(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(question: Question, myAnswer: String): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = questionRepository.answerQuestion(accessToken, question, myAnswer)

        if (result is Resource.Success) {
            mixpanelRepository.answerQuestion()
        }

        return result
    }
}