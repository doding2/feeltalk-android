package com.clonect.feeltalk.new_domain.model.chat

data class SendVoiceChatDto(
    val index: Long,
    val pageIndex: Long,
    val isRead: Boolean,
    val createAt: String
): java.io.Serializable