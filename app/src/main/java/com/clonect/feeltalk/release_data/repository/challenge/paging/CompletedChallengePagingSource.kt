package com.clonect.feeltalk.release_data.repository.challenge.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_data.mapper.toChallengeList
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeListDto
import com.clonect.feeltalk.release_domain.model.challenge.LastChallengePageNoDto
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CompletedChallengePagingSource(
    private val tokenRepository: TokenRepository,
    private val remoteDataSource: ChallengeRemoteDataSource
): PagingSource<Long, Challenge>() {

    override fun getRefreshKey(state: PagingState<Long, Challenge>): Long? {
        val page: Long? = runBlocking(Dispatchers.IO) {
            try {
                val result = getLastCompletedPageNo()
                result.pageNo
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                infoLog("completed challenge paging error in getRefreshKey(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
                null
            }
        }
        return page
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Challenge> {
        return try {
            val pageKey = params.key
                ?: getLastCompletedPageNo().pageNo

            val result = getCompletedList(pageKey).toChallengeList()
            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = if (pageKey <= 0) null else pageKey - 1
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            infoLog("completed challenge paging error in load(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
            LoadResult.Error(e)
        }
    }



    private suspend fun getAccessToken(): String {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            throw tokenInfo.throwable
        }
        return (tokenInfo as Resource.Success).data.accessToken
    }

    private suspend fun getLastCompletedPageNo(): LastChallengePageNoDto {
        return remoteDataSource.getLastCompletedChallengePageNo(getAccessToken())
    }

    private suspend fun getCompletedList(pageNo: Long): ChallengeListDto {
        return remoteDataSource.getCompletedChallengeList(getAccessToken(), pageNo)
    }
}