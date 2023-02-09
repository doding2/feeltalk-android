package com.clonect.feeltalk.data.repository.question.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.domain.model.dto.question.QuestionDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response

class QuestionRemoteDataSourceImpl(
    private val clonectService: ClonectService
): QuestionRemoteDataSource {
    override suspend fun getTodayQuestion(accessToken: String): Response<QuestionDto> {
        val body = JsonObject()
        body.addProperty("accessToken", accessToken)
        val response = clonectService.getTodayQuestion(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }
}