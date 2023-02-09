package com.clonect.feeltalk.data.repository.question.datasource

import com.clonect.feeltalk.domain.model.data.question.Question

interface QuestionCacheDataSource {

    fun getTodayQuestion(): Question?
    fun saveTodayQuestion(todayQuestion: Question)

}