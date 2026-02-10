package com.clonect.feeltalk.release_data.repository.challenge.dataSource

interface ChallengeLocalDataSource {

    suspend fun setChallengeUpdated(isUpdated: Boolean)
    suspend fun getChallengeUpdated(): Boolean

}