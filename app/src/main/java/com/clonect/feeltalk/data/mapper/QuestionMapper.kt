package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.QuestionDto
import java.text.SimpleDateFormat
import java.util.*

fun QuestionDto.toQuestion(): Question {
    val format = SimpleDateFormat("yyyy/MM/dd/hh/mm", Locale.getDefault())
    val date = format.format(Date())

    return Question(
        contentPrefix = "",
        content = question,
        contentSuffix = "",
        questionDate = date,
        myAnswer = "",
        partnerAnswer = "",
        myAnswerDate = "",
        partnerAnswerDate = ""
    )
}