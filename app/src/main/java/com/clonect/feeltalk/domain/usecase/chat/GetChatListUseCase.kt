package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatListUseCase(private val chatRepository: ChatRepository) {

    operator fun invoke(questionContent: String): Flow<Resource<List<Chat>>> {
        return chatRepository.getChatListByQuestion(questionContent)
    }

}