package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat
import retrofit2.Response

interface ChatRemoteDataSource {

    suspend fun getChatListByQuestionId(questionId: Long): Response<List<Chat>>

    suspend fun sendChat(chat: Chat): Response<String>

}