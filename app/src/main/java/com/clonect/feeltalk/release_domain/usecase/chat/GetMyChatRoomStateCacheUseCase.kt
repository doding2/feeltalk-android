package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository

class GetMyChatRoomStateCacheUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(): Boolean {
        return chatRepository.getMyChatRoomStateCache()
    }
}