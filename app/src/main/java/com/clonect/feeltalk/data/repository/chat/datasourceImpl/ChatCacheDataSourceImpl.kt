package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.domain.model.data.chat.Chat

class ChatCacheDataSourceImpl: ChatCacheDataSource {

    private var chatListMap = mutableMapOf<String, MutableList<Chat>>()

    override fun getChatListByQuestion(questionContent: String): List<Chat>? {
        return chatListMap[questionContent]
    }

    override fun saveChatListToCacheByQuestion(questionContent: String, chatList: List<Chat>) {
        chatListMap[questionContent] = chatList.toMutableList()
    }

    override fun saveOneChatToCache(chat: Chat) {
        val chatList = chatListMap[chat.question]
        if (chatList != null) {
            chatList.add(chat)
            return
        }
        val newList = mutableListOf(chat)
        chatListMap[chat.question] = newList
    }

}