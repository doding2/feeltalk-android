package com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_domain.model.chat.Chat

class ChatCacheDataSourceImpl: ChatCacheDataSource {

    private var chatList: MutableList<Chat> = mutableListOf()

    override fun changeChatList(list: List<Chat>) {
        chatList = list.toMutableList()
    }

    override fun insertChatList(list: List<Chat>) {
        chatList = chatList.run {
            addAll(list)
            val newList = distinctBy { it.index }
                .toMutableList()
            newList.sortBy { it.index }
            newList
        }
    }

    override fun getChatList(pageNo: Long): List<Chat> {
        return chatList.filter {
            it.pageNo == pageNo
        }
    }

    override fun getChatListAll(): List<Chat> = chatList

}