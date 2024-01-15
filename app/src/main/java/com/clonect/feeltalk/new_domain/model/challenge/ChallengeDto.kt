package com.clonect.feeltalk.new_domain.model.challenge

data class ChallengeDto(
    val index: Long,
    val pageNo: Long,
    val title: String,
    val content: String?,
    val creator: String,
    val deadline: String,
    val isCompleted: Boolean,
    val completeDate: String? = null
)
