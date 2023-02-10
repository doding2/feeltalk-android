package com.clonect.feeltalk.domain.model.data.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatTable")
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val question: String,
    val ownerEmail: String,
    val content: String,
    val date: String,
    val isAnswer: Boolean = false
)
