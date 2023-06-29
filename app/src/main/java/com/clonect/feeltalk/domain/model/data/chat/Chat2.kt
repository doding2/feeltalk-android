package com.clonect.feeltalk.domain.model.data.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatTable")
data class Chat2(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val question: String,
    val owner: String,
    val message: String,
    val date: String,
    val isAnswer: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Chat2) return false
        if (other === this) return true
        return id == other.id
                && question == other.question
                && owner == other.owner
                && message == other.message
                && isAnswer == other.isAnswer
    }
}

