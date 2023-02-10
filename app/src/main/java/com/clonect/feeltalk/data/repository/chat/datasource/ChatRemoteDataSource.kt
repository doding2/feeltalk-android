package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat
import retrofit2.Response

interface ChatRemoteDataSource {

    suspend fun getChatListByQuestion(questionContent: String): Response<List<Chat>>

    suspend fun sendChat(chat: Chat): Response<String>

}