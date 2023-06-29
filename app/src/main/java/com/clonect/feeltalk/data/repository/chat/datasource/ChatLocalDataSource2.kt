package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat2
import kotlinx.coroutines.flow.Flow

interface ChatLocalDataSource2 {

    fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat2>>

    suspend fun saveOneChatToDatabase(chat2: Chat2): Long

    suspend fun getChatListByQuestion(questionContent: String): List<Chat2>

    suspend fun insertOrUpdate(question: String, chat2List: List<Chat2>)

    suspend fun deleteByQuestion(questionContent: String)
}