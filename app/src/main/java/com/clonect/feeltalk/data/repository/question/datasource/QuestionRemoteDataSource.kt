package com.clonect.feeltalk.data.repository.question.datasource

import com.clonect.feeltalk.domain.model.dto.question.QuestionDto
import retrofit2.Response

interface QuestionRemoteDataSource {

    suspend fun getTodayQuestion(accessToken: String): Response<QuestionDto>

}