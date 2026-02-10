package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.release_domain.model.chat.Chat
import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetNewChatFlowUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(): Flow<Chat> {
        return chatRepository.getNewChatFlow()
    }
}