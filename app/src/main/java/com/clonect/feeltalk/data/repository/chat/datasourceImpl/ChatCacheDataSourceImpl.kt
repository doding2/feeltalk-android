package com.clonect.feeltalk.data.repository.chat.datasourceImpl

import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.domain.model.chat.Chat

class ChatCacheDataSourceImpl: ChatCacheDataSource {

    private var chatListMap = mutableMapOf<Long, MutableList<Chat>>()

    override fun getChatListByQuestionId(questionId: Long): List<Chat>? {
        return chatListMap[questionId]
    }

    override fun saveChatListToCacheByQuestionId(questionId: Long, chatList: List<Chat>) {
        chatListMap[questionId] = chatList.toMutableList()
    }

    override fun saveOneChatToCache(chat: Chat) {
        val chatList = chatListMap[chat.questionId]
        if (chatList != null) {
            chatList.add(chat)
            return
        }
        val newList = mutableListOf(chat)
        chatListMap[chat.questionId] = newList
    }

}