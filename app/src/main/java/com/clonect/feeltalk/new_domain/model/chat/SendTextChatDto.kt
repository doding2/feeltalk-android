package com.clonect.feeltalk.new_domain.model.chat

data class SendTextChatDto(
    val index: Long,
    val isRead: Boolean,
    val createAt: String
): java.io.Serializable