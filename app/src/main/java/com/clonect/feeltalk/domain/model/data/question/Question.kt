package com.clonect.feeltalk.domain.model.data.question

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "QuestionTable")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contentPrefix: String = "",
    val content: String = "",
    val contentSuffix: String = "",
    var questionDate: String = "",
    var myAnswer: String = "",
    val partnerAnswer: String = "",
    var myAnswerDate: String = "",
    val partnerAnswerDate: String ="",
): Serializable
