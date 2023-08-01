package com.clonect.feeltalk.new_data.repository.question.dataSource

import com.clonect.feeltalk.new_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.new_domain.model.question.QuestionDto
import com.clonect.feeltalk.new_domain.model.question.QuestionListDto

interface QuestionRemoteDataSource {
    suspend fun getLastQuestionPageNo(accessToken: String): LastQuestionPageNoDto
    suspend fun getQuestionList(accessToken: String, pageNo: Long): QuestionListDto

    suspend fun getQuestion(accessToken: String, index: Long): QuestionDto
    suspend fun getTodayQuestion(accessToken: String): QuestionDto

    suspend fun answerQuestion(accessToken: String, index: Long, myAnswer: String)
    suspend fun pressForAnswer(accessToken: String, index: Long)
}