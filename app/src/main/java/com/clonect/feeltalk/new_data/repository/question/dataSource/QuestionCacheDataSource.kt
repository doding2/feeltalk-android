package com.clonect.feeltalk.new_data.repository.question.dataSource

import com.clonect.feeltalk.new_domain.model.question.Question

interface QuestionCacheDataSource {

    fun saveTodayQuestion(todayQuestion: Question)
    fun getTodayQuestion(): Question?

}