package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class ChangeChatRoomStateUseCase(
    private val tokenRepository: TokenRepository,
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(isInChat: Boolean): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return chatRepository.changeChatRoomState(accessToken, isInChat)
    }
}