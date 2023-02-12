package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.QuestionListDto

fun QuestionListDto.toQuestionList(): List<Question> {
    val list = mutableListOf<Question>()
    chattingRoomList.forEach {
        list.add(
            Question(
                question = it.title,
                questionDate = it.createAt
                    .replace(". ", "/")
                    .replace(".", ""),
                myAnswer = it.answer
            )
        )
    }
    return list
}