package com.clonect.feeltalk.new_data.repository.chat.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.clonect.feeltalk.common.Constants
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
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class ChatRemoteMediator(
    private val tokenRepository: TokenRepository,
    private val chatCacheDataSource: ChatCacheDataSource,
    private val chatRemoteDataSource: ChatRemoteDataSource
): RemoteMediator<Long, Chat>() {

    override suspend fun initialize(): InitializeAction {
        return if (chatCacheDataSource.getChatListAll().isEmpty()) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Long, Chat>): MediatorResult {
        return try {
            infoLog("loadType: $loadType")
            val loadKey = when (loadType) {
                LoadType.REFRESH -> getLastChatPageNo().pageNo
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    lastItem.pageNo
                }
            }

            val chatList = withContext(Dispatchers.IO) {
                getChatList(loadKey).toChatList()
            }

            if (loadType == LoadType.REFRESH) {
                chatCacheDataSource.changeChatList(chatList)
            }
            else {
                chatCacheDataSource.insertChatList(chatList)
            }
            infoLog("mediator loading done")

            MediatorResult.Success(chatList.isEmpty() || chatList.size < Constants.CHAT_PAGE_SIZE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
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