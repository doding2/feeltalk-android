package com.clonect.feeltalk.domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.ChatRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class ReloadChatListUseCase(private val userRepository: UserRepository, private val chatRepository: ChatRepository) {

    suspend operator fun invoke(questionContent: String): Resource<String>{
        val result = userRepository.getAccessToken()
        return if (result is Resource.Success) {
            chatRepository.reloadChatListOfQuestion(result.data, questionContent)
        } else {
            Resource.Error(Exception("User is not logged in yet."))
        }
    }

}