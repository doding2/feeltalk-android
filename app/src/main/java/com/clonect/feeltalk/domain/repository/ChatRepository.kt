package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatListByQuestion(questionContent: String): Flow<Resource<List<Chat>>>

    suspend fun sendChat(chat: Chat): Resource<String>

    /* Only For FCM Service */
    suspend fun saveChat(chat: Chat): Resource<Long>

}