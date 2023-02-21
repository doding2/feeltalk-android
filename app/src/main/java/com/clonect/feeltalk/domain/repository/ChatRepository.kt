package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatListByQuestion(accessToken: String, questionContent: String): Flow<Resource<List<Chat>>>

    suspend fun reloadChatListOfQuestion(accessToken: String, questionContent: String): Resource<String>

    suspend fun sendChat(accessToken: String, chat: Chat): Resource<SendChatDto>

    /* Only For FCM Service */
    suspend fun saveChat(chat: Chat): Resource<Long>

}