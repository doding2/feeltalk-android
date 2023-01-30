package com.clonect.feeltalk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clonect.feeltalk.domain.model.chat.Chat

@Database(entities = [Chat::class], version = 1)
abstract class FeeltalkDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao

}