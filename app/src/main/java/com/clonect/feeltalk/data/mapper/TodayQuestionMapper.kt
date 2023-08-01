package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionDto

fun TodayQuestionDto.toQuestion(): Question2 {
    return Question2(
        question = todayQuestion
    )
}