package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto2
import kotlinx.coroutines.flow.Flow

interface ChatRepository2 {

    fun getChatListByQuestion(accessToken: String, questionContent: String): Flow<Resource<List<Chat2>>>

    suspend fun reloadChatListOfQuestion(accessToken: String, questionContent: String): Resource<String>

    suspend fun sendChat(accessToken: String, chat2: Chat2): Resource<SendChatDto2>

    /* Only For FCM Service */
    suspend fun saveChat(chat2: Chat2): Resource<Long>

}