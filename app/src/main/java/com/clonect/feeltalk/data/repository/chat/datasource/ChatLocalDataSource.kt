package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat
import kotlinx.coroutines.flow.Flow

interface ChatLocalDataSource {

    fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat>>

    suspend fun saveChatList(chatList: List<Chat>): List<Long>

    suspend fun saveOneChatToDatabase(chat: Chat): Long

    suspend fun getChatListByQuestion(questionContent: String): List<Chat>

}