package com.clonect.feeltalk.release_domain.model.chat

data class ChatQuestionDto(
    val index: Long,
    val title: String,
    val selfAnswer: String?,
    val partnerAnswer: String?,
    val createAt: String
): java.io.Serializable
