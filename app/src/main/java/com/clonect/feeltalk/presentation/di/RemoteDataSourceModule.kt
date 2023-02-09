package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatRemoteDataSourceImpl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionRemoteDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionRemoteDataSourceImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataSourceModule {

    @Singleton
    @Provides
    fun providesUserRemoteDataSource(
        clonectService: ClonectService
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesChatRemoteDataSource(
        clonectService: ClonectService
    ): ChatRemoteDataSource {
        return ChatRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesQuestionRemoteDataSource(
        clonectService: ClonectService
    ): QuestionRemoteDataSource {
        return QuestionRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesEncryptionRemoteDataSource(
        clonectService: ClonectService
    ): EncryptionRemoteDataSource {
        return EncryptionRemoteDataSourceImpl(clonectService)
    }


}