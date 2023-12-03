package com.clonect.feeltalk.new_domain.model.question

data class PressForAnswerChatResponse(
    val index: Long,
    val pageIndex: Long,
    val isRead: Boolean,
    val createAt: String
)
