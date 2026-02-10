package com.clonect.feeltalk.release_data.repository.challenge

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_data.mapper.toChallenge
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeCacheDataSource
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeLocalDataSource
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.release_data.repository.challenge.paging.CompletedChallengePagingSource
import com.clonect.feeltalk.release_data.repository.challenge.paging.OngoingChallengePagingSource
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeChatResponse
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeListDto
import com.clonect.feeltalk.release_domain.model.challenge.LastChallengePageNoDto
import com.clonect.feeltalk.release_domain.model.challenge.ShareChallengeChatResponse
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

class ChallengeRepositoryImpl(
    private val cacheDataSource: ChallengeCacheDataSource,
    private val localDataSource: ChallengeLocalDataSource,
    private val remoteDataSource: ChallengeRemoteDataSource,
    private val ongoingPagingSource: OngoingChallengePagingSource,
    private val completedPagingSource: CompletedChallengePagingSource,
): ChallengeRepository {
    override suspend fun getLastOngoingChallengePageNo(accessToken: String): Resource<LastChallengePageNoDto> {
        return try {
            val result = remoteDataSource.getLastOngoingChallengePageNo(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getOngoingChallengeList(
        accessToken: String,
        pageNo: Long,
    ): Resource<ChallengeListDto> {
        return try {
            val result = remoteDataSource.getOngoingChallengeList(accessToken, pageNo)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getPagingOngoingChallenge(): Flow<PagingData<Challenge>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.ONGOING_CHALLENGE_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            ongoingPagingSource
        }.flow
    }

    override suspend fun getLastCompletedChallengePageNo(accessToken: String): Resource<LastChallengePageNoDto> {
        return try {
            val result = remoteDataSource.getLastCompletedChallengePageNo(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getCompletedChallengeList(
        accessToken: String,
        pageNo: Long,
    ): Resource<ChallengeListDto> {
        return try {
            val result = remoteDataSource.getCompletedChallengeList(accessToken, pageNo)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getPagingCompletedChallenge(): Flow<PagingData<Challenge>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.ONGOING_CHALLENGE_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            completedPagingSource
        }.flow
    }

    override suspend fun addMyChallenge(
        accessToken: String,
        title: String,
        deadline: String,
        content: String,
    ): Resource<ChallengeChatResponse> {
        return try {
            val result = remoteDataSource.addChallenge(accessToken, title, deadline, content)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            cacheDataSource.addChallenge(
                Challenge(
                    index = result.coupleChallenge.index,
                    title = result.coupleChallenge.challengeTitle,
                    body = result.coupleChallenge.challengeBody,
                    deadline = format.parse(deadline),
                    owner = result.coupleChallenge.creator,
                    isCompleted = false,
                    isNew = true
                )
            )
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun modifyChallenge(
        accessToken: String,
        index: Long,
        title: String,
        deadline: String,
        content: String,
        owner: String
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.modifyChallenge(accessToken, index, title, deadline, content)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            cacheDataSource.modifyChallenge(
                Challenge(
                    index = index,
                    title = title,
                    body = content,
                    deadline = format.parse(deadline),
                    owner = owner,
                    isCompleted = false
                )
            )
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteChallenge(accessToken: String, challenge: Challenge): Resource<Unit> {
        return try {
            val result = remoteDataSource.deleteChallenge(accessToken, challenge.index)
            cacheDataSource.deleteChallenge(challenge)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun completeChallenge(accessToken: String, challenge: Challenge): Resource<ChallengeChatResponse> {
        return try {
            val result = remoteDataSource.completeChallenge(accessToken, challenge.index)
            cacheDataSource.deleteChallenge(challenge)
            cacheDataSource.addChallenge(challenge.copy(isCompleted = true, completeDate = Date()))
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getChallenge(accessToken: String, index: Long): Resource<Challenge> {
        return try {
            val result = remoteDataSource.getChallenge(accessToken, index).toChallenge()
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getChallengeCount(accessToken: String): Resource<ChallengeCountDto> {
        return try {
            val result = remoteDataSource.getChallengeCount(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun addPartnerChallengeCache(challenge: Challenge) {
        cacheDataSource.addChallenge(challenge)
        localDataSource.setChallengeUpdated(true)
        cacheDataSource.setChallengeUpdated(true)
    }
    override suspend fun getAddChallengeFlow(): Flow<Challenge> {
        return cacheDataSource.getAddChallengeFlow()
    }

    override suspend fun deletePartnerChallengeCache(challenge: Challenge) {
        cacheDataSource.deleteChallenge(challenge)
        localDataSource.setChallengeUpdated(true)
        cacheDataSource.setChallengeUpdated(true)
    }
    override suspend fun getDeleteChallengeFlow(): Flow<Challenge> {
        return cacheDataSource.getDeleteChallengeFlow()
    }

    override suspend fun modifyPartnerChallengeCache(challenge: Challenge) {
        cacheDataSource.modifyChallenge(challenge)
        localDataSource.setChallengeUpdated(true)
        cacheDataSource.setChallengeUpdated(true)
    }
    override suspend fun getModifyChallengeFlow(): Flow<Challenge> {
        return cacheDataSource.getModifyChallengeFlow()
    }

    override suspend fun setChallengeUpdated(isUpdated: Boolean): Resource<Unit> {
        return try {
            localDataSource.setChallengeUpdated(isUpdated)
            cacheDataSource.setChallengeUpdated(isUpdated)
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
    override suspend fun getChallengeUpdatedFlow(): Flow<Boolean> {
        val local = localDataSource.getChallengeUpdated()
        cacheDataSource.setChallengeUpdated(local)
        return cacheDataSource.getChallengeUpdatedFlow()
    }

    override suspend fun shareChallenge(
        accessToken: String,
        index: Long,
    ): Resource<ShareChallengeChatResponse> {
        return try {
            val result = remoteDataSource.shareChallenge(accessToken, index)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}