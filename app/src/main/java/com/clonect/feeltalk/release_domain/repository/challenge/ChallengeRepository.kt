package com.clonect.feeltalk.release_domain.repository.challenge

import androidx.paging.PagingData
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.challenge.*
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    suspend fun getLastOngoingChallengePageNo(accessToken: String): Resource<LastChallengePageNoDto>
    suspend fun getOngoingChallengeList(accessToken: String, pageNo: Long): Resource<ChallengeListDto>
    fun getPagingOngoingChallenge(): Flow<PagingData<Challenge>>

    suspend fun getLastCompletedChallengePageNo(accessToken: String): Resource<LastChallengePageNoDto>
    suspend fun getCompletedChallengeList(accessToken: String, pageNo: Long): Resource<ChallengeListDto>
    fun getPagingCompletedChallenge(): Flow<PagingData<Challenge>>

    suspend fun addMyChallenge(accessToken: String, title: String, deadline: String, content: String): Resource<ChallengeChatResponse>
    suspend fun modifyChallenge(accessToken: String, index: Long, title: String, deadline: String, content: String, owner: String): Resource<Unit>
    suspend fun deleteChallenge(accessToken: String, challenge: Challenge): Resource<Unit>
    suspend fun completeChallenge(accessToken: String, challenge: Challenge): Resource<ChallengeChatResponse>

    suspend fun getChallenge(accessToken: String, index: Long): Resource<Challenge>
    suspend fun getChallengeCount(accessToken: String): Resource<ChallengeCountDto>

    suspend fun addPartnerChallengeCache(challenge: Challenge)
    suspend fun getAddChallengeFlow(): Flow<Challenge>

    suspend fun deletePartnerChallengeCache(challenge: Challenge)
    suspend fun getDeleteChallengeFlow(): Flow<Challenge>

    suspend fun modifyPartnerChallengeCache(challenge: Challenge)
    suspend fun getModifyChallengeFlow(): Flow<Challenge>

    suspend fun setChallengeUpdated(isUpdated: Boolean): Resource<Unit>
    suspend fun getChallengeUpdatedFlow(): Flow<Boolean>

    suspend fun shareChallenge(accessToken: String, index: Long): Resource<ShareChallengeChatResponse>
}