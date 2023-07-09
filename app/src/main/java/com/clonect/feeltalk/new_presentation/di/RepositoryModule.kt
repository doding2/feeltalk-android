package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.data.repository.chat.ChatRepository2Impl
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource2
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource2
import com.clonect.feeltalk.data.repository.encryption.EncryptionRepositoryImpl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.data.repository.news.NewsRepositoryImpl
import com.clonect.feeltalk.data.repository.question.QuestionRepositoryImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.data.repository.user.UserRepositoryImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.repository.*
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.chat.ChatRepositoryImpl
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatLocalDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.signIn.SignInRepositoryImpl
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInCacheDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInLocalDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInRemoteDataSource
import com.clonect.feeltalk.new_data.repository.token.TokenRepositoryImpl
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenLocalDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun providesSignInRepository(
        cacheDataSource: SignInCacheDataSource,
        localDataSource: SignInLocalDataSource,
        remoteDataSource: SignInRemoteDataSource
    ): SignInRepository {
        return SignInRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource)
    }

    @Singleton
    @Provides
    fun providesTokenRepository(
        cacheDataSource: TokenCacheDataSource,
        localDataSource: TokenLocalDataSource,
        remoteDataSource: TokenRemoteDataSource
    ): TokenRepository {
        return TokenRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource)
    }

    @Singleton
    @Provides
    fun providesChatRepository(
        remoteDataSource: ChatRemoteDataSource,
        localDataSource: ChatLocalDataSource,
        cacheDataSource: ChatCacheDataSource,
        tokenRepository: TokenRepository,
    ): ChatRepository {
        return ChatRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource, tokenRepository)
    }




    /** Old **/

    @Singleton
    @Provides
    fun providesUserRepository(
        remoteDataSource: UserRemoteDataSource,
        localDataSource: UserLocalDataSource,
        cacheDataSource: UserCacheDataSource
    ): UserRepository {
        return UserRepositoryImpl(remoteDataSource, localDataSource, cacheDataSource)
    }

    @Singleton
    @Provides
    fun providesChatRepository2(
        remoteDataSource: ChatRemoteDataSource2,
        localDataSource: ChatLocalDataSource2,
        cacheDataSource: ChatCacheDataSource2,
        userLevelEncryptHelper: UserLevelEncryptHelper,
    ): ChatRepository2 {
        return ChatRepository2Impl(remoteDataSource, localDataSource, cacheDataSource, userLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesQuestionRepository(
        remoteDataSource: QuestionRemoteDataSource,
        localDataSource: QuestionLocalDataSource,
        cacheDataSource: QuestionCacheDataSource,
        userLevelEncryptHelper: UserLevelEncryptHelper
    ): QuestionRepository {
        return QuestionRepositoryImpl(remoteDataSource, localDataSource, cacheDataSource, userLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesEncryptionRepository(
        remoteDataSource: EncryptionRemoteDataSource,
        localDataSource: EncryptionLocalDataSource,
        cacheDataSource: EncryptionCacheDataSource,
        messageEncryptHelper: MessageEncryptHelper
    ): EncryptionRepository {
        return EncryptionRepositoryImpl(remoteDataSource, localDataSource, cacheDataSource, messageEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesNewsRepository(
        clonectService: ClonectService
    ): NewsRepository {
        return NewsRepositoryImpl(clonectService)
    }

}