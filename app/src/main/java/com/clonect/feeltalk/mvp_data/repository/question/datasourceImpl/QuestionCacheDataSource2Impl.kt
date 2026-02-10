package com.clonect.feeltalk.mvp_data.repository.question.datasourceImpl

import com.clonect.feeltalk.mvp_data.repository.question.datasource.QuestionCacheDataSource2
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2

class QuestionCacheDataSource2Impl: QuestionCacheDataSource2 {

    private var todayQuestion2: Question2? = null
    private val question2Map: MutableMap<String, Question2> = mutableMapOf()

    override fun getTodayQuestion(): Question2? = todayQuestion2

    override fun saveTodayQuestion(todayQuestion2: Question2) {
        this.todayQuestion2 = todayQuestion2
        question2Map[todayQuestion2.question] = todayQuestion2
    }


    override fun getQuestionList(): List<Question2> {
        return question2Map.values.toList()
    }

    override fun saveQuestionList(question2List: List<Question2>) {
        question2List.forEach {
            question2Map[it.question] = it
        }
    }

    override fun saveOneQuestion(question2: Question2) {
        question2Map[question2.question] = question2
    }

}