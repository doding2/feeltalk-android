package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetPartnerChatRoomStateFlowUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return chatRepository.getPartnerChatRoomStateFlow()
    }
}