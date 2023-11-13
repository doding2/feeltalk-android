package com.clonect.feeltalk.new_data.repository.challenge.dataSource

interface ChallengeLocalDataSource {

    suspend fun setChallengeUpdated(isUpdated: Boolean)
    suspend fun getChallengeUpdated(): Boolean

}