package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository

class ChangePartnerChatRoomStateCacheUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(state: Boolean) {
        return chatRepository.changePartnerChatRoomStateCache(state)
    }
}