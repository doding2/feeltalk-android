package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository

class GetMyChatRoomStateCacheUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(): Boolean {
        return chatRepository.getMyChatRoomStateCache()
    }
}