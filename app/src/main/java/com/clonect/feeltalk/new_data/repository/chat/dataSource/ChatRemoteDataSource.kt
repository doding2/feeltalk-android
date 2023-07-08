package com.clonect.feeltalk.new_data.repository.chat.dataSource

import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto

interface ChatRemoteDataSource {

    suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean)
    suspend fun getLastChatPageNo(accessToken: String): LastChatPageNoDto
    suspend fun getChatList(accessToken: String, pageNo: Long): ChatListDto
    suspend fun sendTextChat(accessToken: String, message: String): SendTextChatDto

}