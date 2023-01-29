package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.chat.Chat
import kotlinx.coroutines.flow.Flow

interface ChatLocalDataSource {

    fun getChatListByQuestionId(questionId: Long): List<Chat>

    suspend fun saveOneChatToDatabase(chat: Chat): Long

}