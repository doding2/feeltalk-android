package com.clonect.feeltalk.data.db

import androidx.room.*
import com.clonect.feeltalk.domain.model.data.chat.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatList(chatList: List<Chat>): List<Long>

    @Update
    suspend fun updateChatList(chatList: List<Chat>): Int

    @Query("SELECT * FROM ChatTable WHERE question == :questionContent")
    fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat>>

    @Query("SELECT * FROM ChatTable WHERE question == :questionContent")
    suspend fun getChatListByQuestion(questionContent: String): List<Chat>

}