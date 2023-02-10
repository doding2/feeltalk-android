package com.clonect.feeltalk.data.repository.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val cacheDataSource: ChatCacheDataSource
): ChatRepository {

    override fun getChatListByQuestion(questionContent: String): Flow<Resource<List<Chat>>> = flow {
        val cache = getChatListFromCache(questionContent)
        emit(cache)

        val localFlow = getChatListFlowFromDB(questionContent)
        if (localFlow is Resource.Success) {
            localFlow.data.collect {
                cacheDataSource.saveChatListToCacheByQuestion(questionContent, it)
                emit(Resource.Success(it))
            }
        }
        if (localFlow is Resource.Error) {
            emit(Resource.Error(localFlow.throwable))
        }
        if (localFlow is Resource.Loading) {
            emit(Resource.Loading(localFlow.isLoading))
        }

        val remote = getChatListFromServer(questionContent)
        if (remote is Resource.Success) {
            saveChatListToDB(questionContent, remote.data)
            cacheDataSource.saveChatListToCacheByQuestion(questionContent, remote.data)
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



    private suspend fun saveChatListToDB(questionContent: String, newList: List<Chat>) {
        val oldList = localDataSource.getChatListByQuestion(questionContent)
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

    private fun getChatListFromCache(questionContent: String): Resource<List<Chat>> {
        return try {
            val chatList = cacheDataSource.getChatListByQuestion(questionContent)
                ?: throw NullPointerException("Chat list is not saved at cache yet.")
            Resource.Success(chatList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun getChatListFlowFromDB(questionString: String): Resource<Flow<List<Chat>>> {
        return try {
            val chatList = localDataSource.getChatListFlowByQuestion(questionString)
            Resource.Success(chatList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getChatListFromServer(questionString: String): Resource<List<Chat>> {
        return try {
            val response = remoteDataSource.getChatListByQuestion(questionString)
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