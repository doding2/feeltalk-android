package com.clonect.feeltalk.mvp_domain.model.data.question

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "QuestionTable")
data class Question2(
    @PrimaryKey
    val question: String,
    var questionDate: String? = null,
    var myAnswer: String? = null,
    var myAnswerDate: String? = null,
    var partnerAnswer: String? = null,
    val partnerAnswerDate: String? = null,
    val viewType: String = "item",
    var isFirstOpen: Boolean = true,
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (other !is Question2) return false
        if (other === this) return true
        return question == other.question
                && viewType == other.viewType
    }
}
