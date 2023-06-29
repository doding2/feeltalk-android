package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat2

interface ChatCacheDataSource2 {

    fun getChatListByQuestion(questionContent: String): List<Chat2>?

    fun saveChatListToCacheByQuestion(questionContent: String, chat2List: List<Chat2>)

    fun saveOneChatToCache(chat2: Chat2)

}