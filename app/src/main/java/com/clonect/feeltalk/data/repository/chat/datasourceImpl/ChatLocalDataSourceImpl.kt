package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.flow.Flow

class ChatLocalDataSourceImpl(
    private val chatDao: ChatDao
): ChatLocalDataSource {

    override fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat>> = chatDao.getChatListFlowByQuestion(questionContent)

    override suspend fun saveOneChatToDatabase(chat: Chat): Long = chatDao.insertChat(chat)

    override suspend fun getChatListByQuestion(questionContent: String): List<Chat> = chatDao.getChatListByQuestion(questionContent)

    override suspend fun insertOrUpdate(question: String, chatList: List<Chat>) {
        val dbList = getChatListByQuestion(question)

        val oldChat = chatList.subList(0, dbList.size)
        chatDao.updateChatList(oldChat)
        infoLog("updated: ${oldChat.joinToString { it.message }}")

        val newChat = chatList.subList(dbList.size, chatList.size)
        chatDao.insertChatList(newChat)
        infoLog("inserted: ${newChat.joinToString { it.message }}")
    }

    override suspend fun clear(): Boolean {
        TODO("Not yet implemented")
    }

}