package com.clonect.feeltalk.release_data.repository.question.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.release_data.api.ClonectService
import com.clonect.feeltalk.release_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.release_domain.model.chat.ShareQuestionChatDto
import com.clonect.feeltalk.release_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.release_domain.model.question.QuestionDto
import com.clonect.feeltalk.release_domain.model.question.QuestionListDto
import com.google.gson.JsonObject
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import com.clonect.feeltalk.release_domain.model.question.PressForAnswerChatResponse

class QuestionRemoteDataSourceImpl(
    private val clonectService: ClonectService
): QuestionRemoteDataSource {
    override suspend fun getLastQuestionPageNo(accessToken: String): LastQuestionPageNoDto {
        val response = clonectService.getLastQuestionPageNo(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getQuestionList(accessToken: String, pageNo: Long): QuestionListDto {
        val body = JsonObject().apply {
            addProperty("pageNo", pageNo)
        }
        val response = clonectService.getQuestionList(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getQuestion(accessToken: String, index: Long): QuestionDto {
        val response = clonectService.getQuestion(accessToken, index)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getTodayQuestion(accessToken: String): QuestionDto {
        val response = clonectService.getTodayQuestion(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun answerQuestion(accessToken: String, index: Long, myAnswer: String) {
        val body = JsonObject().apply {
            addProperty("index", index)
            addProperty("myAnswer", myAnswer)
        }
        val response = clonectService.answerQuestion(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun pressForAnswer(accessToken: String, index: Long): PressForAnswerChatResponse {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.pressForAnswer(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun shareQuestion(accessToken: String, index: Long): ShareQuestionChatDto {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.shareQuestion(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }
}