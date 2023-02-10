package com.clonect.feeltalk.data.repository.question.datasourceImpl

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response

class QuestionRemoteDataSourceImpl(
    private val clonectService: ClonectService,
    private val userLevelEncryptHelper: UserLevelEncryptHelper
): QuestionRemoteDataSource {

    override suspend fun getTodayQuestion(accessToken: String): Response<QuestionDto> {
        val body = JsonObject()
        body.addProperty("accessToken", accessToken)
        val response = clonectService.getTodayQuestion(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun sendQuestionAnswer(
        accessToken: String,
        question: String,
        answer: String,
    ): Response<SendQuestionDto> {
        val encrypted = when (val result = userLevelEncryptHelper.encryptMyText(answer)) {
            is Resource.Success -> result.data
            is Resource.Error -> throw result.throwable
            is Resource.Loading -> throw Exception("Encryption Failed in send question answer")
        }

        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("question", question)
            addProperty("answer", encrypted)
        }
        val response = clonectService.sendQuestionAnswer(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }
}