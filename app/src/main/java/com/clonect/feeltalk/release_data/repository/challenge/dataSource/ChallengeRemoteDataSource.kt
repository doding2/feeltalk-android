package com.clonect.feeltalk.release_data.repository.challenge.dataSource

import com.clonect.feeltalk.release_domain.model.challenge.*

interface ChallengeRemoteDataSource {
    suspend fun getLastOngoingChallengePageNo(accessToken: String): LastChallengePageNoDto
    suspend fun getOngoingChallengeList(accessToken: String, pageNo: Long): ChallengeListDto

    suspend fun getLastCompletedChallengePageNo(accessToken: String): LastChallengePageNoDto
    suspend fun getCompletedChallengeList(accessToken: String, pageNo: Long): ChallengeListDto

    suspend fun addChallenge(accessToken: String, title: String, deadline: String, content: String): ChallengeChatResponse
    suspend fun modifyChallenge(accessToken: String, index: Long, title: String, deadline: String, content: String)
    suspend fun deleteChallenge(accessToken: String, index: Long)
    suspend fun completeChallenge(accessToken: String, index: Long): ChallengeChatResponse

    suspend fun getChallenge(accessToken: String, index: Long): ChallengeDto
    suspend fun getChallengeCount(accessToken: String): ChallengeCountDto

    suspend fun shareChallenge(accessToken: String, index: Long): ShareChallengeChatResponse
}