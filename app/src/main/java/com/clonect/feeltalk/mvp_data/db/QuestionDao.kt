package com.clonect.feeltalk.mvp_data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM QuestionTable")
    fun getQuestionListFlow(): Flow<List<Question2>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question2: Question2): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionList(question2List: List<Question2>): List<Long>

    @Query("SELECT * FROM QuestionTable WHERE questionDate == :date")
    suspend fun getQuestionListByDate(date: String): List<Question2>

    @Query("SELECT * FROM QuestionTable WHERE question == :content")
    suspend fun getQuestionListByContent(content: String): List<Question2>

    @Query("DELETE FROM QuestionTable")
    suspend fun deleteAll()
}