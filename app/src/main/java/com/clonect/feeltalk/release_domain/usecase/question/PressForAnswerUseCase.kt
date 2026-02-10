package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.question.PressForAnswerChatResponse
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class PressForAnswerUseCase(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(index: Long): Resource<PressForAnswerChatResponse> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return questionRepository.pressForAnswer(accessToken, index)
    }
}