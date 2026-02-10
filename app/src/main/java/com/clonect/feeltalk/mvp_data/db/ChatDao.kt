package com.clonect.feeltalk.mvp_data.db

import androidx.room.*
import com.clonect.feeltalk.mvp_domain.model.data.chat.Chat2
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insertChat(chat2: Chat2): Long

    @Insert
    suspend fun insertChatList(chat2List: List<Chat2>): List<Long>

    @Update
    suspend fun updateChatList(chat2List: List<Chat2>): Int

    @Query("SELECT * FROM ChatTable WHERE question == :questionContent")
    fun getChatListFlowByQuestion(questionContent: String): Flow<List<Chat2>>

    @Query("SELECT * FROM ChatTable WHERE question == :questionContent")
    suspend fun getChatListByQuestion(questionContent: String): List<Chat2>

    @Query("DELETE FROM ChatTable WHERE question == :questionContent")
    suspend fun deleteByQuestion(questionContent: String)

    @Query("DELETE FROM ChatTable")
    suspend fun deleteAll()
}