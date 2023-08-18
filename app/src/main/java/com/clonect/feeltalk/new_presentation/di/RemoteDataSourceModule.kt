package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource2
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatRemoteDataSource2Impl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionRemoteDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource2
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionRemoteDataSource2Impl
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserRemoteDataSourceImpl
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.new_data.repository.challenge.dataSourceImpl.ChallengeRemoteDataSourceImpl
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl.ChatRemoteDataSourceImpl
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSourceImpl.QuestionRemoteDataSourceImpl
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInRemoteDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSourceImpl.SignInRemoteDataSourceImpl
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSourceImpl.TokenRemoteDataSourceImpl
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
    fun providesSignInRemoteDatasource(clonectService: ClonectService): SignInRemoteDataSource {
        return SignInRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesTokenRemoteDataSource(clonectService: ClonectService): TokenRemoteDataSource {
        return TokenRemoteDataSourceImpl(clonectService)
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
    fun providesChallengeRemoteDataSource(
        clonectService: ClonectService
    ): ChallengeRemoteDataSource {
        return ChallengeRemoteDataSourceImpl(clonectService)
    }






    /** old **/

    @Singleton
    @Provides
    fun providesUserRemoteDataSource2(
        clonectService: ClonectService
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesChatRemoteDataSource2(
        clonectService: ClonectService
    ): ChatRemoteDataSource2 {
        return ChatRemoteDataSource2Impl(clonectService)
    }

    @Singleton
    @Provides
    fun providesQuestionRemoteDataSource2(
        clonectService: ClonectService
    ): QuestionRemoteDataSource2 {
        return QuestionRemoteDataSource2Impl(clonectService)
    }

    @Singleton
    @Provides
    fun providesEncryptionRemoteDataSource(
        clonectService: ClonectService
    ): EncryptionRemoteDataSource {
        return EncryptionRemoteDataSourceImpl(clonectService)
    }


}