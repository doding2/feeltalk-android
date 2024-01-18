package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository

class AddNewChatCacheUseCase(
    private val chatRepository: ChatRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(chat: Chat) {
        mixpanelRepository.sendChat()
        return chatRepository.addNewChatCache(chat)
    }
}