package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.chat.Chat
import retrofit2.Response

interface ChatRemoteDataSource {

    suspend fun getChatListByQuestionId(chat: Chat): Response<List<Chat>>

    suspend fun sendChat(chat: Chat): Response<String>

}