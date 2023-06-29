package com.clonect.feeltalk.new_domain.model.chat

data class ChatChallengeDto(
    val index: Long,
    val category: String,
    val challengeTitle: String,
    val challengeBody: String?,
    val deadline: String,
    val creator: String
): java.io.Serializable
