package com.clonect.feeltalk.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clonect.feeltalk.domain.model.data.question.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question): Long

    @Query("SELECT * FROM QuestionTable WHERE questionDate == :date")
    suspend fun getQuestionListByDate(date: String): List<Question>

}