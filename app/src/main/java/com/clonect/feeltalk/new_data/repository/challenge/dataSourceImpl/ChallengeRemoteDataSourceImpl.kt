package com.clonect.feeltalk.new_data.repository.challenge.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.new_domain.model.challenge.AddChallengeDto
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeDto
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeListDto
import com.clonect.feeltalk.new_domain.model.challenge.LastChallengePageNoDto
import com.google.gson.JsonObject

class ChallengeRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChallengeRemoteDataSource {
    override suspend fun getLastOngoingChallengePageNo(accessToken: String): LastChallengePageNoDto {
        val response = clonectService.getLastOngoingChallengePageNo("Bearer $accessToken")
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
        val response = clonectService.getOngoingChallengeList("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getLastCompletedChallengePageNo(accessToken: String): LastChallengePageNoDto {
        val response = clonectService.getLastCompletedChallengePageNo("Bearer $accessToken")
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
        val response = clonectService.getCompletedChallengeList("Bearer $accessToken", body)
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
    ): AddChallengeDto {
        val body = JsonObject().apply {
            addProperty("title", title)
            addProperty("deadline", deadline)
            addProperty("content", content)
        }
        val response = clonectService.addChallenge("Bearer $accessToken", body)
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
        val response = clonectService.modifyChallenge("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun deleteChallenge(accessToken: String, index: Long) {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.deleteChallenge("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun completeChallenge(accessToken: String, index: Long) {
        val body = JsonObject().apply {
            addProperty("index", index)
        }
        val response = clonectService.completeChallenge("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getChallenge(accessToken: String, index: Long): ChallengeDto {
        val response = clonectService.getChallenge("Bearer $accessToken", index)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getChallengeCount(accessToken: String): ChallengeCountDto {
        val response = clonectService.getChallengeCount("Bearer $accessToken")
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }
}