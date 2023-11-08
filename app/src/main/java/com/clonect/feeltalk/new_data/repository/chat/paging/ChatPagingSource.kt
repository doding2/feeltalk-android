package com.clonect.feeltalk.new_data.repository.chat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.mapper.toChallenge
import com.clonect.feeltalk.new_data.mapper.toChatList
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.clonect.feeltalk.presentation.utils.infoLog
import com.navercorp.nid.NaverIdLoginSDK.getAccessToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChatPagingSource(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository,
    private val challengeRepository: ChallengeRepository,
    private val chatRemoteDataSource: ChatRemoteDataSource,
): PagingSource<Long, Chat>() {

    override fun getRefreshKey(state: PagingState<Long, Chat>): Long? {
        val page: Long? = runBlocking(Dispatchers.IO) {
            try {
                val result = getLastChatPageNo()
                result.pageNo
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                infoLog("chat paging error in getRefreshKey(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
                null
            }
        }
        return page
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Chat> {
        return try {
            val pageKey = params.key
                ?: getLastChatPageNo().pageNo

            val result = getChatList(pageKey).toChatList(
                loadQuestion = ::getQuestion,
                loadChallenge = ::getChallenge
            )
            LoadResult.Page(
                data = result,
                prevKey = if (pageKey <= 0) null else pageKey - 1,
                nextKey = null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            infoLog("chat paging error in load(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
            LoadResult.Error(e)
        }
    }


    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            throw tokenInfo.throwable
        }
        (tokenInfo as Resource.Success).data.accessToken
    }

    private suspend fun getLastChatPageNo(): LastChatPageNoDto = withContext(Dispatchers.IO) {
        chatRemoteDataSource.getLastChatPageNo(getAccessToken())
    }

    private suspend fun getChatList(pageNo: Long): ChatListDto = withContext(Dispatchers.IO) {
        chatRemoteDataSource.getChatList(getAccessToken(), pageNo)
    }

    private suspend fun getQuestion(index: Long): Question = withContext(Dispatchers.IO) {
        val result = questionRepository.getQuestion(getAccessToken(), index)
        if (result is Resource.Error) {
            throw result.throwable
        }
        (result as Resource.Success).data
    }

    private suspend fun getChallenge(index: Long): Challenge = withContext(Dispatchers.IO) {
        val result = challengeRepository.getChallenge(getAccessToken(), index)
        if (result is Resource.Error) {
            throw result.throwable
        }
        (result as Resource.Success).data
    }
}