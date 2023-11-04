package com.clonect.feeltalk.new_domain.model.chat

data class ChatDto(
    val index: Long,
    val type: String,
    val chatSender: String,
    val isRead: Boolean,
    val mine: Boolean,
    val createAt: String,
    val message: String?,
    val coupleChallenge: ChatChallengeDto?,
    val coupleQuestion: Long?,
    val url: String?,
    val signal: String?,
): java.io.Serializable
