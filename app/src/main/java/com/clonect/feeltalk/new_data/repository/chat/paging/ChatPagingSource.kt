package com.clonect.feeltalk.new_data.repository.chat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.mapper.toChatList
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class ChatPagingSource(
    private val tokenRepository: TokenRepository,
    private val chatCacheDataSource: ChatCacheDataSource,
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

            val result = getChatList(pageKey).toChatList()
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


    private suspend fun getAccessToken(): String {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            throw tokenInfo.throwable
        }
        return (tokenInfo as Resource.Success).data.accessToken
    }

    private suspend fun getLastChatPageNo(): LastChatPageNoDto {
        return chatRemoteDataSource.getLastChatPageNo(getAccessToken())
    }

    private suspend fun getChatList(pageNo: Long): ChatListDto {
        return chatRemoteDataSource.getChatList(getAccessToken(), pageNo)
    }
}