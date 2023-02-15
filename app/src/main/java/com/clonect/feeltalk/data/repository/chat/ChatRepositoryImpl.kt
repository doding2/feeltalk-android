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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val cacheDataSource: ChatCacheDataSource,
    private val userLevelEncryptHelper: UserLevelEncryptHelper,
): ChatRepository {

    @OptIn(FlowPreview::class)
    override fun getChatListByQuestion(accessToken: String, questionContent: String): Flow<Resource<List<Chat>>> {
        val cacheFlow = channelFlow {
            val cache = cacheDataSource.getChatListByQuestion(questionContent)
            cache?.let { send(Resource.Success(cache)) }
        }

        val localFlow = channelFlow {
            val local = localDataSource.getChatListFlowByQuestion(questionContent)
            local.collectLatest {
                cacheDataSource.saveChatListToCacheByQuestion(questionContent, it)
                send(Resource.Success(it))
            }
        }

        val remoteFlow = channelFlow {
            val remote = getChatListFromServer(accessToken, questionContent)
            if (remote is Resource.Success) {
                val newChatList = remote.data.toChatList(
                    accessToken = accessToken,
                    questionString = questionContent,
                    userLevelEncryptHelper = userLevelEncryptHelper,
                )

                localDataSource.insertOrUpdate(questionContent, newChatList)
                cacheDataSource.saveChatListToCacheByQuestion(questionContent, newChatList)
                send(Resource.Success(newChatList))
            }
            if (remote is Resource.Error) {
                send(Resource.Error(remote.throwable))
            }
        }

        return flowOf(cacheFlow, localFlow, remoteFlow).flattenMerge()
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


    override suspend fun saveChat(chat: Chat): Resource<Long> {
        return try {
            val id = localDataSource.saveOneChatToDatabase(chat)
            Resource.Success(id)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
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