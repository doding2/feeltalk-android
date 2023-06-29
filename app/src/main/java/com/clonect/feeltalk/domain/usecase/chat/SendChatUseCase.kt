package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto2
import com.clonect.feeltalk.domain.repository.ChatRepository2
import com.clonect.feeltalk.domain.repository.UserRepository

class SendChatUseCase(private val userRepository: UserRepository, private val chatRepository2: ChatRepository2) {

    suspend operator fun invoke(chat2: Chat2): Resource<SendChatDto2> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> chatRepository2.sendChat(result.data, chat2)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }

}