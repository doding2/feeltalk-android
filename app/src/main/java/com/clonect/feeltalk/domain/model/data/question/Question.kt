package com.clonect.feeltalk.domain.model.data.question

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuestionTable")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contentPrefix: String = "",
    val content: String = "",
    val contentSuffix: String = "",
    val questionDate: String = "",
    var myAnswer: String = "",
    val partnerAnswer: String = "",
    var myAnswerDate: String = "",
    val partnerAnswerDate: String ="",
): java.io.Serializable
