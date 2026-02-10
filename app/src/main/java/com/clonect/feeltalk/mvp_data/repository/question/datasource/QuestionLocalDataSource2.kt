package com.clonect.feeltalk.mvp_data.repository.question.datasource

import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import kotlinx.coroutines.flow.Flow

interface QuestionLocalDataSource2 {

    suspend fun getQuestionListFlow(): Flow<List<Question2>>

    suspend fun getTodayQuestion(date: String): Question2?
    suspend fun saveOneQuestion(question2: Question2): Long
    suspend fun saveQuestionList(question2List: List<Question2>): List<Long>

    suspend fun getQuestionByContent(content: String): Question2?

}