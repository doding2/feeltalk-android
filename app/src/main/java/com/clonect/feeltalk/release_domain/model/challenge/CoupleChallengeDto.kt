package com.clonect.feeltalk.release_domain.model.challenge

data class CoupleChallengeDto(
    val index: Long,
    val challengeTitle: String,
    val challengeBody: String,
    val deadline: String,
    val creator: String,
)
