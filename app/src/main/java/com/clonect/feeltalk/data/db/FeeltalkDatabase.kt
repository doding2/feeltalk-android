package com.clonect.feeltalk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.data.question.Question

@Database(entities = [Chat2::class, Question::class], version = 1)
abstract class FeeltalkDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao

    abstract fun questionDao(): QuestionDao
}