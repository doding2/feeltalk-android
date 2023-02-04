package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.domain.model.chat.Chat
import kotlinx.coroutines.flow.Flow

class ChatLocalDataSourceImpl(
    private val chatDao: ChatDao
): ChatLocalDataSource {

    override fun getChatListFlowByQuestionId(questionId: Long): Flow<List<Chat>> = chatDao.getChatListFlowByQuestionId(questionId)

    override suspend fun saveOneChatToDatabase(chat: Chat): Long = chatDao.insertChat(chat)

    override suspend fun getChatListByQuestionId(questionId: Long): List<Chat> = chatDao.getChatListByQuestionId(questionId)

}