package com.clonect.feeltalk.mvp_data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clonect.feeltalk.mvp_domain.model.data.chat.Chat2
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2

@Database(entities = [Chat2::class, Question2::class], version = 2, exportSchema = false)
abstract class FeeltalkDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao

    abstract fun questionDao(): QuestionDao
}