package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import kotlinx.coroutines.flow.Flow

class ChatLocalDataSourceImpl(
    private val chatDao: ChatDao
): ChatLocalDataSource {

    override fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat>> = chatDao.getChatListFlowByQuestion(questionContent)

    override suspend fun saveChatList(chatList: List<Chat>): List<Long> {
        return chatDao.insertChatList(chatList)
    }

    override suspend fun saveOneChatToDatabase(chat: Chat): Long = chatDao.insertChat(chat)

    override suspend fun getChatListByQuestion(questionContent: String): List<Chat> = chatDao.getChatListByQuestion(questionContent)

}