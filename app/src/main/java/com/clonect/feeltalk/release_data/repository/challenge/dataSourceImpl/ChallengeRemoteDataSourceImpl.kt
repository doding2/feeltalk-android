package com.clonect.feeltalk.release_data.repository.challenge.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import com.clonect.feeltalk.release_data.api.ClonectService
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeChatResponse
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeDto
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeListDto
import com.clonect.feeltalk.release_domain.model.challenge.LastChallengePageNoDto
import com.clonect.feeltalk.release_domain.model.challenge.ShareChallengeChatResponse
import com.google.gson.JsonObject

class ChallengeRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChallengeRemoteDataSource {
    override suspend fun getLastOngoingChallengePageNo(accessToken: String): LastChallengePageNoDto {
        val response = clonectService.getLastOngoingChallengePageNo(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getOngoingChallengeList(
        accessToken: String,
        pageNo: Long,
    ): ChallengeListDto {
        val body = JsonObject().apply {
            addProperty("pageNo", pageNo)
        }
        val response = clonectService.getOngoingChallengeList(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getLastCompletedChallengePageNo(accessToken: String): LastChallengePageNoDto {
        val response = clonectService.getLastCompletedChallengePageNo(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getCompletedChallengeList(
        accessToken: String,
        pageNo: Long,
    ): ChallengeListDto {
        val body = JsonObject().apply {
            addProperty("pageNo", pageNo)
        }
        val response = clonectService.getCompletedChallengeList(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun addChallenge(
        accessToken: String,
        title: String,
        deadline: String,
        content: String,
    ): ChallengeChatResponse {
        val body = JsonObject().apply {
            addProperty("title", title)
            addProperty("deadline", deadline)
            addProperty("content", content)
        }
        val response = clonectService.addChallenge(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun modifyChallenge(
        accessToken: String,
        index: Long,
        title: String,
        deadline: String,
        content: String,
    ) {
        val body = JsonObject().apply {
            addProperty("index", index)
            addProperty("title", title)
            addProperty("deadline", deadline)
            addProperty("content", content)
        }
        val response = clonectService.modifyChallenge(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun deleteChallenge(accessToken: String, index: Long) {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.deleteChallenge(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun completeChallenge(accessToken: String, index: Long): ChallengeChatResponse {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.completeChallenge(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getChallenge(accessToken: String, index: Long): ChallengeDto {
        val response = clonectService.getChallenge(accessToken, index)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getChallengeCount(accessToken: String): ChallengeCountDto {
        val response = clonectService.getChallengeCount(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun shareChallenge(accessToken: String, index: Long): ShareChallengeChatResponse {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.shareChallenge(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }
}