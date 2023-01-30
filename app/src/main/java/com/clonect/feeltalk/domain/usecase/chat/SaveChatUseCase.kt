package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository

/* Only For FCM Service */
class SaveChatUseCase(private val chatRepository: ChatRepository) {

    suspend operator fun invoke(chat: Chat): Resource<Long> = chatRepository.saveChat(chat)

}