package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository

class SendChatUseCase(private val chatRepository: ChatRepository) {

    suspend operator fun invoke(chat: Chat): Resource<String> {
        return chatRepository.sendChat(chat)
    }

}