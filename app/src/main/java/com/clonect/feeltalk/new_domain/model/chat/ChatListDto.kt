package com.clonect.feeltalk.new_domain.model.chat

data class ChatListDto(
    val page: Long,
    val chatting: List<ChatDto>
): java.io.Serializable
