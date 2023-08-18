package com.clonect.feeltalk.new_data.repository.question.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.new_domain.model.chat.ShareQuestionChatDto
import com.clonect.feeltalk.new_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.new_domain.model.question.QuestionDto
import com.clonect.feeltalk.new_domain.model.question.QuestionListDto
import com.google.gson.JsonObject
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException

class QuestionRemoteDataSourceImpl(
    private val clonectService: ClonectService
): QuestionRemoteDataSource {
    override suspend fun getLastQuestionPageNo(accessToken: String): LastQuestionPageNoDto {
        val response = clonectService.getLastQuestionPageNo("Bearer $accessToken")
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getQuestionList(accessToken: String, pageNo: Long): QuestionListDto {
        val body = JsonObject().apply {
            addProperty("pageNo", pageNo)
        }
        val response = clonectService.getQuestionList("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getQuestion(accessToken: String, index: Long): QuestionDto {
        val response = clonectService.getQuestion("Bearer $accessToken", index)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getTodayQuestion(accessToken: String): QuestionDto {
        val response = clonectService.getTodayQuestion("Bearer $accessToken")
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
        val response = clonectService.answerQuestion("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun pressForAnswer(accessToken: String, index: Long) {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.pressForAnswer("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun shareQuestion(accessToken: String, index: Long): ShareQuestionChatDto {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.shareQuestion("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }
}