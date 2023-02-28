package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.QuestionListDto
import java.text.SimpleDateFormat
import java.util.*

fun QuestionListDto.toQuestionList(): List<Question> {
    val list = mutableListOf<Question>()
    chattingRoomList.forEach {
        val serverFormat = SimpleDateFormat("yyyy. M. d", Locale.getDefault())
        val clientFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        val date = serverFormat.parse(it.createAt)
        val clientDateString = date?.let { it1 -> clientFormat.format(it1) } ?: it.createAt

        list.add(
            Question(
                question = it.title,
                questionDate = clientDateString,
                myAnswer = it.answer
            )
        )
    }
    return list
}