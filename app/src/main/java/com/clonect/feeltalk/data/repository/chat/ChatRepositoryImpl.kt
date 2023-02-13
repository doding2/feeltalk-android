package com.clonect.feeltalk.data.repository.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toChatList
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto
import com.clonect.feeltalk.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val cacheDataSource: ChatCacheDataSource,
    private val userLevelEncryptHelper: UserLevelEncryptHelper,
): ChatRepository {

    override fun getChatListByQuestion(accessToken: String, questionContent: String): Flow<Resource<List<Chat>>> = flow {
//        val cache = cacheDataSource.getChatListByQuestion(questionContent)
//        cache?.let { emit(Resource.Success(cache)) }
//
//        val localFlow = getChatListFlowFromDB(questionContent)
//        if (localFlow is Resource.Success) {
//            localFlow.data.collect {
//                cacheDataSource.saveChatListToCacheByQuestion(questionContent, it)
//                emit(Resource.Success(it))
//            }
//        }
//        if (localFlow is Resource.Error) {
//            emit(Resource.Error(localFlow.throwable))
//        }

        val remote = getChatListFromServer(accessToken, questionContent)
        if (remote is Resource.Success) {
            val newChatList = remote.data.toChatList(
                accessToken = accessToken,
                questionString = questionContent,
                userLevelEncryptHelper = userLevelEncryptHelper,
            )
            saveChatListToDB(questionContent, newChatList)
            cacheDataSource.saveChatListToCacheByQuestion(questionContent, newChatList)
            emit(Resource.Success(newChatList))
        }
        if (remote is Resource.Error) {
            emit(Resource.Error(remote.throwable))
        }
    }

    override suspend fun sendChat(accessToken: String, chat: Chat): Resource<SendChatDto> {
        return try {
            val encryptedChat = chat.copy(message = userLevelEncryptHelper.encryptMyText(chat.message))
            val remote = remoteDataSource.sendChat(accessToken, encryptedChat).body()!!
            localDataSource.saveOneChatToDatabase(chat)
            cacheDataSource.saveOneChatToCache(chat)
            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
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


    private fun getChatListFlowFromDB(questionString: String): Resource<Flow<List<Chat>>> {
        return try {
            val chatList = localDataSource.getChatListFlowByQuestion(questionString)
            Resource.Success(chatList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getChatListFromServer(accessToken: String, questionString: String): Resource<List<ChatListItemDto>> {
        return try {
            val response = remoteDataSource.getChatListByQuestion(accessToken, questionString)
            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}