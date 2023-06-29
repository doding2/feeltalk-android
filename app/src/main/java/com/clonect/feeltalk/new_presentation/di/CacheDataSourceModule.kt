package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatCacheDataSource2Impl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionCacheDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionCacheDataSourceImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserCacheDataSourceImpl
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








    /** Old **/

    @Singleton
    @Provides
    fun providesUserCacheDataSource(): UserCacheDataSource {
        return UserCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesChatCacheDataSource(): ChatCacheDataSource2 {
        return ChatCacheDataSource2Impl()
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