package com.clonect.feeltalk.data.repository.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val cacheDataSource: ChatCacheDataSource
): ChatRepository {

    override fun getChatListByQuestionId(questionId: Long): Flow<Resource<List<Chat>>> = flow {
        val cache = getChatListFromCache(questionId)
        emit(cache)

        val localFlow = getChatListFlowFromDB(questionId)
        if (localFlow is Resource.Success) {
            localFlow.data.collect {
                cacheDataSource.saveChatListToCacheByQuestionId(questionId, it)
                emit(Resource.Success(it))
            }
        }
        if (localFlow is Resource.Error) {
            emit(Resource.Error(localFlow.throwable))
        }
        if (localFlow is Resource.Loading) {
            emit(Resource.Loading(localFlow.isLoading))
        }

        val remote = getChatListFromServer(questionId)
        if (remote is Resource.Success) {
            saveChatListToDB(questionId, remote.data)
            cacheDataSource.saveChatListToCacheByQuestionId(questionId, remote.data)
        }
        emit(remote)
    }

    override suspend fun sendChat(chat: Chat): Resource<String> {
        val result = sendChatToServer(chat)
        if (result is Resource.Success) {
            localDataSource.saveOneChatToDatabase(chat)
            cacheDataSource.saveOneChatToCache(chat)
        }
        // TODO 이 else 파트는 서버 기능이 추가되면 삭제해야됨(디버깅용)
        else {
            localDataSource.saveOneChatToDatabase(chat)
            cacheDataSource.saveOneChatToCache(chat)
        }
        return result
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



    private suspend fun saveChatListToDB(questionId: Long, newList: List<Chat>) {
        val oldList = localDataSource.getChatListByQuestionId(questionId)
        newList.forEach {
            val isNewChat = !oldList.contains(it)
            if (isNewChat) {
                localDataSource.saveOneChatToDatabase(it)
            }
        }
    }

    private suspend fun sendChatToServer(chat: Chat): Resource<String> {
        return try {
            val response = remoteDataSource.sendChat(chat)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")
            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun getChatListFromCache(questionId: Long): Resource<List<Chat>> {
        return try {
            val chatList = cacheDataSource.getChatListByQuestionId(questionId)
                ?: throw NullPointerException("Chat list is not saved at cache yet.")
            Resource.Success(chatList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun getChatListFlowFromDB(questionId: Long): Resource<Flow<List<Chat>>> {
        return try {
            val chatList = localDataSource.getChatListFlowByQuestionId(questionId)
            Resource.Success(chatList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getChatListFromServer(questionId: Long): Resource<List<Chat>> {
        return try {
            val response = remoteDataSource.getChatListByQuestionId(questionId)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")
            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}