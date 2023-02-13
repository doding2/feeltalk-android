package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto
import retrofit2.Response

interface ChatRemoteDataSource {

    suspend fun getChatListByQuestion(accessToken: String, questionContent: String): Response<List<ChatListItemDto>>

    suspend fun sendChat(accessToken: String, chat: Chat): Response<SendChatDto>

}