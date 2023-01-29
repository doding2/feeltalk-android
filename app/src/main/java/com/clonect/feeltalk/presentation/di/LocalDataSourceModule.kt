package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDataSourceModule {

    @Singleton
    @Provides
    fun providesChatLocalDataSource(chatDao: ChatDao): ChatLocalDataSource {
        return ChatLocalDataSourceImpl(chatDao)
    }

}