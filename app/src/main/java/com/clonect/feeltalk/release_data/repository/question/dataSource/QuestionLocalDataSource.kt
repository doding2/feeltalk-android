package com.clonect.feeltalk.release_data.repository.question.dataSource

interface QuestionLocalDataSource {

    fun setQuestionUpdated(isUpdated: Boolean)
    fun getQuestionUpdated(): Boolean

}