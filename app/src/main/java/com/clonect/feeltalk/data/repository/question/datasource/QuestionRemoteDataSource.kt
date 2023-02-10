package com.clonect.feeltalk.data.repository.question.datasource

import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import retrofit2.Response

interface QuestionRemoteDataSource {

    suspend fun getTodayQuestion(accessToken: String): Response<QuestionDto>
    suspend fun sendQuestionAnswer(accessToken: String, question: String, answer: String): Response<SendQuestionDto>

}