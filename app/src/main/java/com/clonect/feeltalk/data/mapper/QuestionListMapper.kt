package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.model.dto.question.QuestionListDto2
import java.text.SimpleDateFormat
import java.util.*

fun QuestionListDto2.toQuestionList(): List<Question2> {
    val list = mutableListOf<Question2>()
    chattingRoomList.forEach {
        val serverFormat = SimpleDateFormat("yyyy. M. d", Locale.getDefault())
        val clientFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        val date = serverFormat.parse(it.createAt)
        val clientDateString = date?.let { it1 -> clientFormat.format(it1) } ?: it.createAt

        list.add(
            Question2(
                question = it.title,
                questionDate = clientDateString,
                myAnswer = it.answer
            )
        )
    }
    return list
}