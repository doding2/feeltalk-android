package com.clonect.feeltalk.release_domain.model.chat

data class ChatChallengeDto(
    val index: Long,
    val challengeTitle: String,
    val challengeBody: String?,
    val deadline: String,
    val creator: String
): java.io.Serializable
