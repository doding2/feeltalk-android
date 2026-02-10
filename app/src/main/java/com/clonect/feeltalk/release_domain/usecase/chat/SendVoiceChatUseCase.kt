package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.chat.SendVoiceChatDto
import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository
import java.io.File

class SendVoiceChatUseCase(
    private val tokenRepository: TokenRepository,
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(voiceFile: File): Resource<SendVoiceChatDto> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return chatRepository.sendVoiceChat(accessToken, voiceFile)
    }
}