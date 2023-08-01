package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.question.QuestionDto
import com.clonect.feeltalk.new_domain.model.question.QuestionListDto

fun QuestionListDto.toQuestionList(): List<Question> {
    val newList = mutableListOf<Question>()
    for (questionDto in questions) {
        newList.add(
            questionDto.toQuestion()
        )
    }
    return newList
}

fun QuestionDto.toQuestion(): Question {
    return this.run {
        Question(
            index = index,
            pageNo = pageNo,
            title = title,
            header = header,
            body = body,
            highlight = highlight,
            createAt = createAt,
            myAnswer = myAnswer,
            partnerAnswer = partnerAnswer
        )
    }
}