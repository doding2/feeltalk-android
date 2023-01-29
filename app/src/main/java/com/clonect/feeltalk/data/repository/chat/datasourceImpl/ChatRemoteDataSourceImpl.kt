package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.domain.model.chat.Chat
import retrofit2.Response

class ChatRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChatRemoteDataSource {

    override suspend fun getChatListByQuestionId(chat: Chat): Response<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendChat(chat: Chat): Response<String> {
        TODO("Not yet implemented")
    }

}