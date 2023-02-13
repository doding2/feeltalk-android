package com.clonect.feeltalk.data.repository.question.datasource

import com.clonect.feeltalk.domain.model.data.question.Question

interface QuestionLocalDataSource {

    suspend fun getTodayQuestion(date: String): Question?
    suspend fun saveOneQuestion(question: Question): Long

    suspend fun getQuestionByContent(content: String): Question?

}