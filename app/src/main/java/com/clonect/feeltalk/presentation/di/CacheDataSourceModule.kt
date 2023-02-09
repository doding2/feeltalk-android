package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatCacheDataSourceImpl
import com.clonect.feeltalk.data.repository.encryption.EncryptionRepositoryImpl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionCacheDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionCacheDataSourceImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserCacheDataSourceImpl
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheDataSourceModule {

    @Singleton
    @Provides
    fun providesUserCacheDataSource(): UserCacheDataSource {
        return UserCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesChatCacheDataSource(): ChatCacheDataSource {
        return ChatCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesEncryptionCacheDataSource(): EncryptionCacheDataSource {
        return EncryptionCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesQuestionCacheDataSource(): QuestionCacheDataSource {
        return QuestionCacheDataSourceImpl()
    }
}