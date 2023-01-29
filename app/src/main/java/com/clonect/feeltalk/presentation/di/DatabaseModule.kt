package com.clonect.feeltalk.presentation.di

import android.content.Context
import androidx.room.Room
import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.db.FeeltalkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesFeeltalkDatabase(@ApplicationContext context: Context): FeeltalkDatabase {
        return Room.databaseBuilder(context, FeeltalkDatabase::class.java, "feeltalkDatabase")
            .build()
    }

    @Provides
    @Singleton
    fun providesChatDao(feeltalkDatabase: FeeltalkDatabase): ChatDao {
        return feeltalkDatabase.chatDao()
    }


}