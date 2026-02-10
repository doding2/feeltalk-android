package com.clonect.feeltalk.release_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.chat.ShareQuestionChatDto
import com.clonect.feeltalk.release_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class ShareQuestionUseCase(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(index: Long): Resource<ShareQuestionChatDto> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = questionRepository.shareQuestion(accessToken, index)

        if (result is Resource.Success) {
            mixpanelRepository.shareContent()
        }

        return result
    }
}