package com.clonect.feeltalk.domain.model.chat

data class Chat(
    val id: Long,
    val ownerEmail: String,
    val content: String,
    val date: String,
    val isAnswer: Boolean = false
)
