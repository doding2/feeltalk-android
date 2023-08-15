package com.clonect.feeltalk.new_domain.model.chat

data class ShareQuestionChatDto(
    val index: Long,
    val pageNo: Long,
    val isRead: Boolean,
    val createAt: String,
    val coupleQuestion: ChatQuestionDto,
): java.io.Serializable
