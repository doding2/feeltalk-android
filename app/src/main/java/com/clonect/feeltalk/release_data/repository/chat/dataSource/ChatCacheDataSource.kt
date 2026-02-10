package com.clonect.feeltalk.release_data.repository.chat.dataSource

import com.clonect.feeltalk.release_domain.model.chat.Chat
import kotlinx.coroutines.flow.Flow

interface ChatCacheDataSource {

    suspend fun addNewChat(chat: Chat)
    suspend fun getNewChatFlow(): Flow<Chat>
    suspend fun changePartnerChatRoomState(isInChat: Boolean)
    suspend fun getPartnerChatRoomStateFlow(): Flow<Boolean>

    suspend fun changeMyChatRoomState(isInChat: Boolean)
    suspend fun getMyChatRoomState(): Boolean

    fun changeChatList(list: List<Chat>)
    fun insertChatList(list: List<Chat>)
    fun getChatList(pageNo: Long): List<Chat>
    fun getChatListAll(): List<Chat>

}