package com.clonect.feeltalk.domain.model.question

data class Question(
    val id: Long?,
    val questionContent: String?,
    val myAnswer: String?,
    val partnerAnswer: String?
)
