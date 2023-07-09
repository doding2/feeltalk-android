package com.clonect.feeltalk.new_data.repository.chat

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatLocalDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.paging.ChatPagingSource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val cacheDataSource: ChatCacheDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val remoteDataSource: ChatRemoteDataSource,
    private val tokenRepository: TokenRepository,
): ChatRepository {

    override suspend fun changeChatRoomState(
        accessToken: String,
        isInChat: Boolean,
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.changeChatRoomState(accessToken, isInChat)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getLastChatPageNo(accessToken: String): Resource<LastChatPageNoDto> {
        return try {
            val result = remoteDataSource.getLastChatPageNo(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun sendTextChat(
        accessToken: String,
        message: String
    ): Resource<SendTextChatDto> {
        return try {
            val result = remoteDataSource.sendTextChat(accessToken, message)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun getChatList(accessToken: String, pageNo: Long): Resource<ChatListDto> {
        return try {
            val result = remoteDataSource.getChatList(accessToken, pageNo)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getPagingChat(): Flow<PagingData<Chat>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            ChatPagingSource(tokenRepository, cacheDataSource, remoteDataSource)
        }.flow
    }
}