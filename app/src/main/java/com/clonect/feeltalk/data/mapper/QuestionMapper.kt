package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionDto
import java.text.SimpleDateFormat
import java.util.*

fun TodayQuestionDto.toQuestion(): Question {
    return Question(
        question = todayQuestion
    )
}