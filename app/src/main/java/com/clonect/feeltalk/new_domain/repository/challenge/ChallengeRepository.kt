package com.clonect.feeltalk.new_domain.repository.challenge

import androidx.paging.PagingData
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.*
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    suspend fun getLastOngoingChallengePageNo(accessToken: String): Resource<LastChallengePageNoDto>
    suspend fun getOngoingChallengeList(accessToken: String, pageNo: Long): Resource<ChallengeListDto>
    fun getPagingOngoingChallenge(): Flow<PagingData<Challenge>>

    suspend fun getLastCompletedChallengePageNo(accessToken: String): Resource<LastChallengePageNoDto>
    suspend fun getCompletedChallengeList(accessToken: String, pageNo: Long): Resource<ChallengeListDto>
    fun getPagingCompletedChallenge(): Flow<PagingData<Challenge>>

    suspend fun addChallenge(accessToken: String, title: String, deadline: String, content: String): Resource<AddChallengeDto>
    suspend fun modifyChallenge(accessToken: String, index: Long, title: String, deadline: String, content: String): Resource<Unit>
    suspend fun deleteChallenge(accessToken: String, index: Long): Resource<Unit>
    suspend fun completeChallenge(accessToken: String, index: Long): Resource<Unit>

    suspend fun getChallenge(accessToken: String, index: Long): Resource<Challenge>
    suspend fun getChallengeCount(accessToken: String): Resource<ChallengeCountDto>
}