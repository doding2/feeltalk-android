package com.clonect.feeltalk.data.repository.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toChatList
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource2
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource2
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto2
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto2
import com.clonect.feeltalk.domain.repository.ChatRepository2
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class ChatRepository2Impl(
    private val remoteDataSource: ChatRemoteDataSource2,
    private val localDataSource: ChatLocalDataSource2,
    private val cacheDataSource: ChatCacheDataSource2,
    private val userLevelEncryptHelper: UserLevelEncryptHelper,
): ChatRepository2 {

    @OptIn(FlowPreview::class)
    override fun getChatListByQuestion(accessToken: String, questionContent: String): Flow<Resource<List<Chat2>>> {
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
//                send(Resource.Success(newChatList))
            }
            if (remote is Resource.Error) {
                send(Resource.Error(remote.throwable))
            }
        }

        return flowOf(cacheFlow, localFlow, remoteFlow).flattenMerge()
    }

    override suspend fun reloadChatListOfQuestion(
        accessToken: String,
        questionContent: String,
    ): Resource<String> {
        val remote = getChatListFromServer(accessToken, questionContent)
        if (remote is Resource.Success) {
            val newChatList = remote.data.toChatList(
                accessToken = accessToken,
                questionString = questionContent,
                userLevelEncryptHelper = userLevelEncryptHelper,
            )

            localDataSource.insertOrUpdate(questionContent, newChatList)
            cacheDataSource.saveChatListToCacheByQuestion(questionContent, newChatList)
            return Resource.Success("Success to reload chat list: $questionContent")
        }

        return Resource.Error(Exception("Fail to reload chat list: $questionContent"))
    }

    override suspend fun sendChat(accessToken: String, chat2: Chat2): Resource<SendChatDto2> {
        return try {
            val encryptedChat = chat2.copy(message = userLevelEncryptHelper.encryptMyText(chat2.message))
            val remote = remoteDataSource.sendChat(accessToken, encryptedChat).body()!!
            localDataSource.saveOneChatToDatabase(chat2)
            cacheDataSource.saveOneChatToCache(chat2)
            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun saveChat(chat2: Chat2): Resource<Long> {
        return try {
            val id = localDataSource.saveOneChatToDatabase(chat2)
            Resource.Success(id)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e)
        }
    }


    private suspend fun getChatListFromServer(accessToken: String, questionString: String): Resource<List<ChatListItemDto2>> {
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