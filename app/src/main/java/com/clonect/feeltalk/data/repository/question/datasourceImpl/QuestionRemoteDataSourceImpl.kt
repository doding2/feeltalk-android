package com.clonect.feeltalk.data.repository.question.datasourceImpl

import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.domain.model.dto.question.*
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response

class QuestionRemoteDataSourceImpl(
    private val clonectService: ClonectService
): QuestionRemoteDataSource {

    override suspend fun getTodayQuestion(accessToken: String): Response<TodayQuestionDto> {
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
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("question", question)
            addProperty("answer", answer)
        }
        val response = clonectService.sendQuestionAnswer(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getQuestionList(accessToken: String): Response<QuestionListDto> {
        val response = clonectService.getChattingRoomList(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getTodayQuestionAnswers(accessToken: String): Response<TodayQuestionAnswersDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.getTodayQuestionAnswers(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getQuestionAnswers(
        accessToken: String,
        question: String,
    ): Response<QuestionAnswersDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("title", question)
        }
        val response = clonectService.getQuestionAnswers(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getQuestionDetail(
        accessToken: String,
        question: String,
    ): Response<QuestionDetailDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("title", question)
        }
        val response = clonectService.getQuestionDetail(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }
}