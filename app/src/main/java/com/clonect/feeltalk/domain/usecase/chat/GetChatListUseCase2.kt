package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.repository.ChatRepository2
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetChatListUseCase2(private val userRepository: UserRepository, private val chatRepository2: ChatRepository2) {

    suspend operator fun invoke(questionContent: String): Flow<Resource<List<Chat2>>> {
        val result = userRepository.getAccessToken()
        return if (result is Resource.Success) {
            chatRepository2.getChatListByQuestion(result.data, questionContent)
        } else {
            flow { emit(Resource.Error(Exception("User is not logged in yet."))) }
        }
    }

}