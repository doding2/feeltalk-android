package com.clonect.feeltalk.release_domain.model.challenge

data class ChallengeChatResponse(
    val index: Long,
    val pageIndex: Long,
    val isRead: Boolean,
    val createAt: String,
    val coupleChallenge: CoupleChallengeDto,
): java.io.Serializable
