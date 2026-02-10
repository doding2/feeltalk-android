package com.clonect.feeltalk.mvp_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.chat.Chat2
import com.clonect.feeltalk.mvp_domain.repository.ChatRepository2

/* Only For FCM Service */
class SaveChatUseCase(private val chatRepository2: ChatRepository2) {

    suspend operator fun invoke(chat2: Chat2): Resource<Long> = chatRepository2.saveChat(chat2)

}