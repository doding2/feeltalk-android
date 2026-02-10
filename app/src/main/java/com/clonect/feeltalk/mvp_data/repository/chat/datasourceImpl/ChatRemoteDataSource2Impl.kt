package com.clonect.feeltalk.mvp_data.repository.chat.datasourceImpl

import com.clonect.feeltalk.mvp_data.repository.chat.datasource.ChatRemoteDataSource2
import com.clonect.feeltalk.mvp_domain.model.data.chat.Chat2
import com.clonect.feeltalk.mvp_domain.model.dto.chat.ChatListItemDto2
import com.clonect.feeltalk.mvp_domain.model.dto.chat.SendChatDto2
import com.clonect.feeltalk.release_data.api.ClonectService
import com.google.gson.JsonObject
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import retrofit2.Response

class ChatRemoteDataSource2Impl(
    private val clonectService: ClonectService
): ChatRemoteDataSource2 {

    override suspend fun getChatListByQuestion(accessToken: String, questionContent: String): Response<List<ChatListItemDto2>> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("question", questionContent)
        }
        val response = clonectService.getChatList(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun sendChat(accessToken: String, chat2: Chat2): Response<SendChatDto2> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("question", chat2.question)
            addProperty("message", chat2.message)
        }
        val response = clonectService.sendChat(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

}