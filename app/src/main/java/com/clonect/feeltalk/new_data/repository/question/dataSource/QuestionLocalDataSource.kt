package com.clonect.feeltalk.new_data.repository.question.dataSource

interface QuestionLocalDataSource {

    fun setQuestionUpdated(isUpdated: Boolean)
    fun getQuestionUpdated(): Boolean

}