package com.clonect.feeltalk.new_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class GetTodayQuestionUseCase(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(): Resource<Question> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return questionRepository.getTodayQuestion(accessToken)
    }
}