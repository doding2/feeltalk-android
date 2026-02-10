package com.clonect.feeltalk.release_domain.model.question

data class PressForAnswerChatResponse(
    val index: Long,
    val pageIndex: Long,
    val isRead: Boolean,
    val createAt: String
)
