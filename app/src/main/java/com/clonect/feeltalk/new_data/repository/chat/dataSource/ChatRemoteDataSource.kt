package com.clonect.feeltalk.new_data.repository.chat.dataSource

import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto
import com.clonect.feeltalk.new_domain.model.chat.SendVoiceChatDto
import java.io.File

interface ChatRemoteDataSource {

    suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean)
    suspend fun getLastChatPageNo(accessToken: String): LastChatPageNoDto
    suspend fun getChatList(accessToken: String, pageNo: Long): ChatListDto
    suspend fun sendTextChat(accessToken: String, message: String): SendTextChatDto
    suspend fun sendVoiceChat(accessToken: String, voiceFile: File): SendVoiceChatDto

}