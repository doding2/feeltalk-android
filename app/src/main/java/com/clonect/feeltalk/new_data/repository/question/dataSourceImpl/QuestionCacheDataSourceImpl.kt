package com.clonect.feeltalk.new_data.repository.question.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionCacheDataSource
import com.clonect.feeltalk.new_domain.model.question.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionCacheDataSourceImpl: QuestionCacheDataSource {

    private var isQuestionUpdated = MutableStateFlow(false)
    private var todayQuestion = MutableStateFlow<Question?>(null)
    private val answerQuestion = MutableSharedFlow<Question>()

    override fun saveTodayQuestion(todayQuestion: Question?) {
        this.todayQuestion.value = todayQuestion
    }
    override fun getTodayQuestion(): Question? = todayQuestion.value
    override suspend fun getTodayQuestionFlow(): Flow<Question?> {
        todayQuestion.value = null
        return todayQuestion.asStateFlow()
    }

    override suspend fun saveAnswerQuestion(question: Question) {
        answerQuestion.emit(question)
    }
    override suspend fun getAnswerQuestionFlow(): Flow<Question> {
        return answerQuestion.asSharedFlow()
    }

    override fun setQuestionUpdated(isUpdated: Boolean) {
        this.isQuestionUpdated.value = isUpdated
    }
    override fun getQuestionUpdatedFlow(): Flow<Boolean> {
        return isQuestionUpdated.asStateFlow()
    }

}