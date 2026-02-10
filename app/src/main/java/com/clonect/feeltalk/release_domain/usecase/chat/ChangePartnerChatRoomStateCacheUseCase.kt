package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository

class ChangePartnerChatRoomStateCacheUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(state: Boolean) {
        return chatRepository.changePartnerChatRoomStateCache(state)
    }
}