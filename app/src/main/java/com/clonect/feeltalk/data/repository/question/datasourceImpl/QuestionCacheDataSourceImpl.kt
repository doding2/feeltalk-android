package com.clonect.feeltalk.data.repository.question.datasourceImpl

import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.domain.model.data.question.Question

class QuestionCacheDataSourceImpl: QuestionCacheDataSource {

    private var todayQuestion: Question? = null

    override fun getTodayQuestion(): Question? = todayQuestion

    override fun saveTodayQuestion(todayQuestion: Question) {
        this.todayQuestion = todayQuestion
    }

}