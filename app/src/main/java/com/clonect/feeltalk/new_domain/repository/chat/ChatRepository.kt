package com.clonect.feeltalk.new_domain.repository.chat

import androidx.paging.PagingData
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean): Resource<Unit>
    suspend fun getLastChatPageNo(accessToken: String): Resource<LastChatPageNoDto>
    suspend fun sendTextChat(accessToken: String, message: String): Resource<SendTextChatDto>

    suspend fun getChatList(accessToken: String, pageNo: Long): Resource<ChatListDto>
    fun getPagingChat(): Flow<PagingData<Chat>>
}