package com.clonect.feeltalk.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.clonect.feeltalk.domain.model.chat.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insertChat(chat: Chat): Long

    @Query("SELECT * FROM ChatTable WHERE questionId == :questionId")
    fun getChatListByQuestionId(questionId: Long): List<Chat>

}