package com.clonect.feeltalk.mvp_data.repository.chat.datasourceImpl

import com.clonect.feeltalk.mvp_data.db.ChatDao
import com.clonect.feeltalk.mvp_data.repository.chat.datasource.ChatLocalDataSource2
import com.clonect.feeltalk.mvp_domain.model.data.chat.Chat2
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import kotlinx.coroutines.flow.Flow

class ChatLocalDataSource2Impl(
    private val chatDao: ChatDao
): ChatLocalDataSource2 {

    override fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat2>> = chatDao.getChatListFlowByQuestion(questionContent)

    override suspend fun saveOneChatToDatabase(chat2: Chat2): Long = chatDao.insertChat(chat2)

    override suspend fun getChatListByQuestion(questionContent: String): List<Chat2> = chatDao.getChatListByQuestion(questionContent)

    override suspend fun insertOrUpdate(question: String, chat2List: List<Chat2>) {
        val dbList = getChatListByQuestion(question)

        if (dbList.size > chat2List.size) {
            deleteByQuestion(question)
            chatDao.insertChatList(chat2List)
            infoLog("deleted chats in: ${question}")
            infoLog("and re-insert chat list: ${chat2List.joinToString { it.message }}")
            return
        }

        val oldChat = chat2List.subList(0, dbList.size)
        for (i in dbList.indices) {
            oldChat[i].id = dbList[i].id
        }
        chatDao.updateChatList(oldChat)
        infoLog("updated: ${oldChat.joinToString { it.message }}")

        val newChat = chat2List.subList(dbList.size, chat2List.size)
        chatDao.insertChatList(newChat)
        infoLog("inserted: ${newChat.joinToString { it.message }}")
    }


    override suspend fun deleteByQuestion(questionContent: String) {
        chatDao.deleteByQuestion(questionContent)
    }

}