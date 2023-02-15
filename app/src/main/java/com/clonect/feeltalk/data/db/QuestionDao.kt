package com.clonect.feeltalk.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clonect.feeltalk.domain.model.data.question.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM QuestionTable")
    fun getQuestionListFlow(): Flow<List<Question>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionList(questionList: List<Question>): List<Long>

    @Query("SELECT * FROM QuestionTable WHERE questionDate == :date")
    suspend fun getQuestionListByDate(date: String): List<Question>

    @Query("SELECT * FROM QuestionTable WHERE question == :content")
    suspend fun getQuestionListByContent(content: String): List<Question>

}