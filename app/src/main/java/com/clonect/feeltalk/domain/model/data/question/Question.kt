package com.clonect.feeltalk.domain.model.data.question

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "QuestionTable")
data class Question(
    @PrimaryKey
    val question: String,
    var questionDate: String? = null,
    var myAnswer: String? = null,
    var myAnswerDate: String? = null,
    val partnerAnswer: String? = null,
    val partnerAnswerDate: String? = null,
    val viewType: String = "item",
): Serializable
