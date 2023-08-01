package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatCacheDataSource2Impl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionCacheDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource2
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionCacheDataSource2Impl
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserCacheDataSourceImpl
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl.ChatCacheDataSourceImpl
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionCacheDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSourceImpl.QuestionCacheDataSourceImpl
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInCacheDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSourceImpl.SignInCacheDataSourceImpl
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSourceImpl.TokenCacheDataSourceImpl
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
    fun providesSignInCacheDatasource(): SignInCacheDataSource {
        return SignInCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesTokenCacheDataSource(): TokenCacheDataSource {
        return TokenCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesChatCacheDataSource(): ChatCacheDataSource {
        return ChatCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesQuestionCacheDataSource(): QuestionCacheDataSource {
        return QuestionCacheDataSourceImpl()
    }






    /** Old **/

    @Singleton
    @Provides
    fun providesUserCacheDataSource(): UserCacheDataSource {
        return UserCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesChatCacheDataSource2(): ChatCacheDataSource2 {
        return ChatCacheDataSource2Impl()
    }

    @Singleton
    @Provides
    fun providesEncryptionCacheDataSource(): EncryptionCacheDataSource {
        return EncryptionCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesQuestionCacheDataSource2(): QuestionCacheDataSource2 {
        return QuestionCacheDataSource2Impl()
    }
}