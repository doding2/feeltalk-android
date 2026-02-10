package com.clonect.feeltalk.mvp_data.mapper

import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import com.clonect.feeltalk.mvp_domain.model.dto.question.TodayQuestionDto

fun TodayQuestionDto.toQuestion(): Question2 {
    return Question2(
        question = todayQuestion
    )
}