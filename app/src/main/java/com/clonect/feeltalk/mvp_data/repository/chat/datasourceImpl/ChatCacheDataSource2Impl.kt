package com.clonect.feeltalk.mvp_data.repository.chat.datasourceImpl

import com.clonect.feeltalk.mvp_data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.mvp_domain.model.data.chat.Chat2

class ChatCacheDataSource2Impl: ChatCacheDataSource2 {

    private var chat2ListMap = mutableMapOf<String, MutableList<Chat2>>()

    override fun getChatListByQuestion(questionContent: String): List<Chat2>? {
        return chat2ListMap[questionContent]
    }

    override fun saveChatListToCacheByQuestion(questionContent: String, chat2List: List<Chat2>) {
        chat2ListMap[questionContent] = chat2List.toMutableList()
    }

    override fun saveOneChatToCache(chat2: Chat2) {
        val chatList = chat2ListMap[chat2.question]
        if (chatList != null) {
            chatList.add(chat2)
            return
        }
        val newList = mutableListOf(chat2)
        chat2ListMap[chat2.question] = newList
    }

}