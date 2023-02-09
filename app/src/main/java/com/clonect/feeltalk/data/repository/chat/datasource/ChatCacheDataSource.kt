package com.clonect.feeltalk.data.repository.chat.datasource

import com.clonect.feeltalk.domain.model.data.chat.Chat

interface ChatCacheDataSource {

    fun getChatListByQuestionId(questionId: Long): List<Chat>?

    fun saveChatListToCacheByQuestionId(questionId: Long, chatList: List<Chat>)

    fun saveOneChatToCache(chat: Chat)

}