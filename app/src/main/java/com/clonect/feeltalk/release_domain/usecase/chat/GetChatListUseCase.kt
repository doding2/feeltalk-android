package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.chat.ChatListDto
import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class GetChatListUseCase(
    private val tokenRepository: TokenRepository,
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(pageNo: Long): Resource<ChatListDto> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return chatRepository.getChatList(accessToken, pageNo)
    }
}