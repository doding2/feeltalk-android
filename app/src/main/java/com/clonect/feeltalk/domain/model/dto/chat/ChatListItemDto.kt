package com.clonect.feeltalk.domain.model.dto.chat

data class ChatListItemDto(
    val name: String,
    val accessToken: String,
    val message: String,
    val time: String,
)