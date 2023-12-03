package com.clonect.feeltalk.new_domain.model.challenge

data class CoupleChallengeDto(
    val index: Long,
    val challengeTitle: String,
    val challengeBody: String,
    val deadline: String,
    val creator: String,
)
