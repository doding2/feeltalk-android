package com.clonect.feeltalk.new_domain.model.chat

data class ChatQuestionDto(
    val index: Long,
    val questionTitle: String,
    val selfAnswer: String?,
    val partnerAnswer: String?
): java.io.Serializable
