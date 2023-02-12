package com.clonect.feeltalk.data.repository.question.datasource

import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionListDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionDto
import retrofit2.Response

interface QuestionRemoteDataSource {

    suspend fun getTodayQuestion(accessToken: String): Response<TodayQuestionDto>
    suspend fun sendQuestionAnswer(accessToken: String, question: String, answer: String): Response<SendQuestionDto>
    suspend fun getQuestionList(accessToken: String): Response<QuestionListDto>
    suspend fun getTodayQuestionAnswers(accessToken: String): Response<QuestionAnswersDto>
}