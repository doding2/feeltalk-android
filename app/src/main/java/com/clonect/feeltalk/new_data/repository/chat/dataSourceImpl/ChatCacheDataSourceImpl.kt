package com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatCacheDataSourceImpl: ChatCacheDataSource {

    private var newChatFlow = MutableSharedFlow<Chat>()
    private var partnerChatRoomStateFlow = MutableSharedFlow<Boolean>()
    private var myChatRoomStateFlow = false
    private var chatList: MutableList<Chat> = mutableListOf()

    override suspend fun addNewChat(chat: Chat) {
        newChatFlow.emit(chat)
    }

    override suspend fun getNewChatFlow(): Flow<Chat> {
        return newChatFlow.asSharedFlow()
    }

    override suspend fun changePartnerChatRoomState(isInChat: Boolean) {
        partnerChatRoomStateFlow.emit(isInChat)
    }

    override suspend fun getPartnerChatRoomStateFlow(): Flow<Boolean> {
        return partnerChatRoomStateFlow.asSharedFlow()
    }

    override suspend fun changeMyChatRoomState(isInChat: Boolean) {
        myChatRoomStateFlow = isInChat
    }

    override suspend fun getMyChatRoomState(): Boolean {
        return myChatRoomStateFlow
    }


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