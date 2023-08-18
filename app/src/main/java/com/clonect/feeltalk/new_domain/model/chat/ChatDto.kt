package com.clonect.feeltalk.new_domain.model.chat

data class ChatDto(
    val index: Long,
    val type: String,
    val chatSender: String,
    val isRead: Boolean,
    val mine: Boolean,
    val createAt: String,
    val message: String?,
    val coupleChallenge: Long?,
    val coupleQuestion: Long?,
    val emoji: String?,
    val url: String?,
): java.io.Serializable
