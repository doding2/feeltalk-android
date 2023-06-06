package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto
import com.clonect.feeltalk.domain.repository.ChatRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class SendChatUseCase(private val userRepository: UserRepository, private val chatRepository: ChatRepository) {

    suspend operator fun invoke(chat: Chat): Resource<SendChatDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> chatRepository.sendChat(result.data, chat)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }

}