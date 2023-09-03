package com.clonect.feeltalk.new_data.repository.challenge

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeCacheDataSource
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeLocalDataSource
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.new_data.repository.challenge.paging.CompletedChallengePagingSource
import com.clonect.feeltalk.new_data.repository.challenge.paging.OngoingChallengePagingSource
import com.clonect.feeltalk.new_domain.model.challenge.AddChallengeDto
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeDto
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeListDto
import com.clonect.feeltalk.new_domain.model.challenge.LastChallengePageNoDto
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun addChallenge(
        accessToken: String,
        title: String,
        deadline: String,
        content: String,
    ): Resource<AddChallengeDto> {
        return try {
            val result = remoteDataSource.addChallenge(accessToken, title, deadline, content)
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
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.modifyChallenge(accessToken, index, title, deadline, content)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteChallenge(accessToken: String, index: Long): Resource<Unit> {
        return try {
            val result = remoteDataSource.deleteChallenge(accessToken, index)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun completeChallenge(accessToken: String, index: Long): Resource<Unit> {
        return try {
            val result = remoteDataSource.completeChallenge(accessToken, index)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getChallenge(accessToken: String, index: Long): Resource<ChallengeDto> {
        return try {
            val result = remoteDataSource.getChallenge(accessToken, index)
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
}