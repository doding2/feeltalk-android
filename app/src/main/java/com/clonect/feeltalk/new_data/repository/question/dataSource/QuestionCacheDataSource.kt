package com.clonect.feeltalk.new_data.repository.question.dataSource

import com.clonect.feeltalk.new_domain.model.question.Question
import kotlinx.coroutines.flow.Flow

interface QuestionCacheDataSource {

    fun saveTodayQuestion(todayQuestion: Question?)
    fun getTodayQuestion(): Question?
    suspend fun getTodayQuestionFlow(): Flow<Question?>

    suspend fun saveAnswerQuestion(question: Question)
    suspend fun getAnswerQuestionFlow(): Flow<Question>

    fun setQuestionUpdated(isUpdated: Boolean)
    fun getQuestionUpdatedFlow(): Flow<Boolean>
}