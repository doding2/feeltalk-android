package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.domain.model.chat.Chat
import retrofit2.Response

class ChatRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChatRemoteDataSource {

    // TODO 서버랑 연결하기
    override suspend fun getChatListByQuestionId(questionId: Long): Response<List<Chat>> {
        throw Exception("아직 서버 기능을 안 만들었음")
    }

    override suspend fun sendChat(chat: Chat): Response<String> {
        throw Exception("아직 서버 기능을 안 만들었음")
    }

}