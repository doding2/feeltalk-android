package com.clonect.feeltalk.new_data.repository.challenge.dataSource

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import kotlinx.coroutines.flow.Flow

interface ChallengeCacheDataSource {
    suspend fun addChallenge(challenge: Challenge)
    suspend fun getAddChallengeFlow(): Flow<Challenge>

    suspend fun deleteChallenge(challenge: Challenge)
    suspend fun getDeleteChallengeFlow(): Flow<Challenge>

    suspend fun modifyChallenge(challenge: Challenge)
    suspend fun getModifyChallengeFlow(): Flow<Challenge>

    suspend fun setChallengeUpdated(isUpdated: Boolean)
    suspend fun getChallengeUpdatedFlow(): Flow<Boolean>
}