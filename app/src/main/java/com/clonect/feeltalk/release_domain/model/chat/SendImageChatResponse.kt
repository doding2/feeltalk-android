package com.clonect.feeltalk.release_domain.model.chat

data class SendImageChatResponse(
    val index: Long,
    val pageIndex: Long,
    val isRead: Boolean,
    val createAt: String
): java.io.Serializable