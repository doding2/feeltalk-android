package com.clonect.feeltalk.release_data.repository.challenge.dataSourceImpl

import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeCacheDataSource
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ChallengeCacheDataSourceImpl: ChallengeCacheDataSource {

    private val isChallengeUpdated = MutableStateFlow(false)
    private val addedChallenge = MutableSharedFlow<Challenge>()
    private val deletedChallenge = MutableSharedFlow<Challenge>()
    private val editedChallenge = MutableSharedFlow<Challenge>()

    override suspend fun addChallenge(challenge: Challenge) {
        addedChallenge.emit(challenge)
    }
    override suspend fun getAddChallengeFlow(): Flow<Challenge> {
        return addedChallenge.asSharedFlow()
    }

    override suspend fun deleteChallenge(challenge: Challenge) {
        deletedChallenge.emit(challenge)
    }
    override suspend fun getDeleteChallengeFlow(): Flow<Challenge> {
        return deletedChallenge.asSharedFlow()
    }

    override suspend fun modifyChallenge(challenge: Challenge) {
        editedChallenge.emit(challenge)
    }
    override suspend fun getModifyChallengeFlow(): Flow<Challenge> {
        return editedChallenge.asSharedFlow()
    }

    override suspend fun setChallengeUpdated(isUpdated: Boolean) {
        isChallengeUpdated.value = isUpdated
    }
    override suspend fun getChallengeUpdatedFlow(): Flow<Boolean> {
        return isChallengeUpdated.asStateFlow()
    }
}