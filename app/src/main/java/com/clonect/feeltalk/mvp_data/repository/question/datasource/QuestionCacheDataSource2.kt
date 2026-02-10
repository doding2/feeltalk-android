package com.clonect.feeltalk.mvp_data.repository.question.datasource

import com.clonect.feeltalk.mvp_domain.model.data.question.Question2

interface QuestionCacheDataSource2 {

    fun getTodayQuestion(): Question2?
    fun saveTodayQuestion(todayQuestion2: Question2)

    fun getQuestionList(): List<Question2>
    fun saveQuestionList(question2List: List<Question2>)
    fun saveOneQuestion(question2: Question2)

}