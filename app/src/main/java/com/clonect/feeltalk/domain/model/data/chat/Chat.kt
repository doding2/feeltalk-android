package com.clonect.feeltalk.domain.model.data.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatTable")
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val owner: String,
    val message: String,
    val date: String,
    val isAnswer: Boolean = false
)
