package com.clonect.feeltalk.release_data.repository.question.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_data.mapper.toQuestionList
import com.clonect.feeltalk.release_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.release_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.model.question.QuestionListDto
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class QuestionPagingSource(
    private val tokenRepository: TokenRepository,
    private val questionRemoteDataSource: QuestionRemoteDataSource,
): PagingSource<Long, Question>() {

    override fun getRefreshKey(state: PagingState<Long, Question>): Long? {
        val page: Long? = runBlocking(Dispatchers.IO) {
            try {
                val result = getLastQuestionPageNo()
                result.pageNo
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                infoLog("question paging error in getRefreshKey(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
                null
            }
        }
        return page
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Question> {
        return try {
            val pageKey = params.key
                ?: getLastQuestionPageNo().pageNo

            val result = getQuestionList(pageKey).toQuestionList()
            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = if (pageKey <= 0) null else pageKey - 1
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            infoLog("question paging error in load(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
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

    private suspend fun getLastQuestionPageNo(): LastQuestionPageNoDto {
        return questionRemoteDataSource.getLastQuestionPageNo(getAccessToken())
    }

    private suspend fun getQuestionList(pageNo: Long): QuestionListDto {
        return questionRemoteDataSource.getQuestionList(getAccessToken(), pageNo)
    }
}