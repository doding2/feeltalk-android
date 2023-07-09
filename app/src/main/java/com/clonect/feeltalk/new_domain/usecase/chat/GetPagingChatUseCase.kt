package com.clonect.feeltalk.new_domain.usecase.chat

import androidx.paging.PagingData
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetPagingChatUseCase(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(): Flow<PagingData<Chat>> {
        return chatRepository.getPagingChat()
    }
}