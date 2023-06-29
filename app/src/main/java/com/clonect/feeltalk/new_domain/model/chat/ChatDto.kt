package com.clonect.feeltalk.new_domain.model.chat

data class ChatDto(
    val index: Long,
    val type: String,
    val chatSender: String,
    val isRead: Boolean,
    val createAt: String,
    val message: String?,
    val coupleChallenge: ChatChallengeDto?,
    val coupleQuestion: ChatQuestionDto?,
    val emoji: String?,
    val url: String?,
): java.io.Serializable
