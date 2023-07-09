package com.clonect.feeltalk.new_data.repository.chat.dataSource

import com.clonect.feeltalk.new_domain.model.chat.Chat

interface ChatCacheDataSource {

    fun changeChatList(list: List<Chat>)
    fun insertChatList(list: List<Chat>)
    fun getChatList(pageNo: Long): List<Chat>
    fun getChatListAll(): List<Chat>

}