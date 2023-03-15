package com.clonect.feeltalk.data.repository.question.datasource

import com.clonect.feeltalk.domain.model.dto.question.*
import retrofit2.Response

interface QuestionRemoteDataSource {

    suspend fun getTodayQuestion(accessToken: String): Response<TodayQuestionDto>
    suspend fun sendQuestionAnswer(accessToken: String, question: String, answer: String): Response<SendQuestionDto>
    suspend fun getQuestionList(accessToken: String): Response<QuestionListDto>
    suspend fun getTodayQuestionAnswers(accessToken: String): Response<TodayQuestionAnswersDto>

    suspend fun getQuestionAnswers(accessToken: String, question: String): Response<QuestionAnswersDto>
    suspend fun getQuestionDetail(accessToken: String, question: String): Response<QuestionDetailDto>
}