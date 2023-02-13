package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response

class ChatRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChatRemoteDataSource {

    override suspend fun getChatListByQuestion(accessToken: String, questionContent: String): Response<List<ChatListItemDto>> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("question", questionContent)
        }
        val response = clonectService.getChatList(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun sendChat(accessToken: String, chat: Chat): Response<SendChatDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("question", chat.question)
            addProperty("message", chat.message)
        }
        val response = clonectService.sendChat(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

}