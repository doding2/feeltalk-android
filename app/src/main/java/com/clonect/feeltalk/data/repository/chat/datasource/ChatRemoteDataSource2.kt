package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto2
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto2
import retrofit2.Response

interface ChatRemoteDataSource2 {

    suspend fun getChatListByQuestion(accessToken: String, questionContent: String): Response<List<ChatListItemDto2>>

    suspend fun sendChat(accessToken: String, chat2: Chat2): Response<SendChatDto2>

}