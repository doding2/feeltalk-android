package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetChatListUseCase(private val userRepository: UserRepository, private val chatRepository: ChatRepository) {

    suspend operator fun invoke(questionContent: String): Flow<Resource<List<Chat>>> {
        val result = userRepository.getAccessToken()
        return if (result is Resource.Success) {
            chatRepository.getChatListByQuestion(result.data, questionContent)
        } else {
            flow { emit(Resource.Error(Exception("User is not logged in yet."))) }
        }
    }

}