package com.clonect.feeltalk.new_domain.model.question

data class QuestionDto(
    val index: Long,
    val pageNo: Long,
    val title: String,
    val header: String,
    val body: String,
    val highlight: List<Long>,
    val createAt: String,
    val myAnswer: String?,
    val partnerAnswer: String?,
    val isMyAnswer: Boolean,
    val isPartnerAnswer: Boolean,
)
