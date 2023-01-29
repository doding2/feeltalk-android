package com.clonect.feeltalk.data.repository.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.domain.model.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val cacheDataSource: ChatCacheDataSource
): ChatRepository {

    override fun getChatListByQuestionId(questionId: Long): Flow<Resource<List<Chat>>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendChat(chat: Chat): Resource<String> {
        TODO("Not yet implemented")
    }

    /* Only For FCM Service */
    override suspend fun saveChat(chat: Chat): Resource<Long> {
        return try {
            val id = localDataSource.saveOneChatToDatabase(chat)
            Resource.Success(id)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e)
        }
    }

}