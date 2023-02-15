package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat
import kotlinx.coroutines.flow.Flow

interface ChatLocalDataSource {

    fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat>>

    suspend fun saveOneChatToDatabase(chat: Chat): Long

    suspend fun getChatListByQuestion(questionContent: String): List<Chat>

    suspend fun insertOrUpdate(question: String, chatList: List<Chat>)

    suspend fun clear(): Boolean
}