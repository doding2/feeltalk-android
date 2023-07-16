package com.clonect.feeltalk.new_domain.repository.chat

import androidx.paging.PagingData
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.*
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChatRepository {

    suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean): Resource<Unit>
    suspend fun getLastChatPageNo(accessToken: String): Resource<LastChatPageNoDto>
    suspend fun sendTextChat(accessToken: String, message: String): Resource<SendTextChatDto>
    suspend fun sendVoiceChat(accessToken: String, voiceFile: File): Resource<SendVoiceChatDto>

    suspend fun getChatList(accessToken: String, pageNo: Long): Resource<ChatListDto>
    fun getPagingChat(): Flow<PagingData<Chat>>
}