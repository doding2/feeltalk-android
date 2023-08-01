package com.clonect.feeltalk.new_data.repository.question.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionCacheDataSource
import com.clonect.feeltalk.new_domain.model.question.Question

class QuestionCacheDataSourceImpl: QuestionCacheDataSource {

    private var todayQuestion: Question? = null

    override fun saveTodayQuestion(todayQuestion: Question) {
        this.todayQuestion = todayQuestion
    }

    override fun getTodayQuestion(): Question? = todayQuestion

}