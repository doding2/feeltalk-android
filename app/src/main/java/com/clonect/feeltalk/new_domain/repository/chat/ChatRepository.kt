package com.clonect.feeltalk.new_domain.repository.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto

interface ChatRepository {

    suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean): Resource<Unit>
    suspend fun getLastChatPageNo(accessToken: String): Resource<LastChatPageNoDto>
    suspend fun getChatList(accessToken: String, pageNo: Long): Resource<ChatListDto>
    suspend fun sendTextChat(accessToken: String, message: String): Resource<SendTextChatDto>

}