package com.clonect.feeltalk.new_domain.model.challenge

data class ShareChallengeChatResponse(
    val coupleChallenge: CoupleChallengeDto,
    val index: Long,
    val isRead: Boolean,
    val createAt: String
)