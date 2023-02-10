package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat

interface ChatCacheDataSource {

    fun getChatListByQuestion(questionContent: String): List<Chat>?

    fun saveChatListToCacheByQuestion(questionContent: String, chatList: List<Chat>)

    fun saveOneChatToCache(chat: Chat)

}