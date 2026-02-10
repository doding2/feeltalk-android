package com.clonect.feeltalk.mvp_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.ChatRepository2
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class ReloadChatListUseCase(private val userRepository: UserRepository, private val chatRepository2: ChatRepository2) {

    suspend operator fun invoke(questionContent: String): Resource<String>{
        val result = userRepository.getAccessToken()
        return if (result is Resource.Success) {
            chatRepository2.reloadChatListOfQuestion(result.data, questionContent)
        } else {
            Resource.Error(Exception("User is not logged in yet."))
        }
    }

}