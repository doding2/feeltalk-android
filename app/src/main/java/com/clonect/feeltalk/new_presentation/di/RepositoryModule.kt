package com.clonect.feeltalk.new_presentation.di

import android.content.Context
import com.clonect.feeltalk.data.repository.chat.ChatRepository2Impl
import com.clonect.feeltalk.data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource2
import com.clonect.feeltalk.data.repository.chat.datasource.ChatRemoteDataSource2
import com.clonect.feeltalk.data.repository.encryption.EncryptionRepositoryImpl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.data.repository.news.NewsRepositoryImpl
import com.clonect.feeltalk.data.repository.question.QuestionRepository2Impl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource2
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource2
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource2
import com.clonect.feeltalk.data.repository.user.UserRepositoryImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.repository.*
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.challenge.ChallengeRepositoryImpl
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeCacheDataSource
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeLocalDataSource
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.new_data.repository.challenge.paging.CompletedChallengePagingSource
import com.clonect.feeltalk.new_data.repository.challenge.paging.OngoingChallengePagingSource
import com.clonect.feeltalk.new_data.repository.chat.ChatRepositoryImpl
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatLocalDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.chat.paging.ChatPagingSource
import com.clonect.feeltalk.new_data.repository.question.QuestionRepositoryImpl
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionCacheDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionLocalDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.new_data.repository.question.paging.QuestionPagingSource
import com.clonect.feeltalk.new_data.repository.account.AccountRepositoryImpl
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountCacheDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountLocalDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountRemoteDataSource
import com.clonect.feeltalk.new_data.repository.mixpanel.MixpanelRepositoryImpl
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelCacheDataSource
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelLocalDataSource
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelRemoteDataSource
import com.clonect.feeltalk.new_data.repository.partner.PartnerRepositoryImpl
import com.clonect.feeltalk.new_data.repository.partner.dataSource.PartnerCacheDataSource
import com.clonect.feeltalk.new_data.repository.partner.dataSource.PartnerLocalDataSource
import com.clonect.feeltalk.new_data.repository.partner.dataSource.PartnerRemoteDataSource
import com.clonect.feeltalk.new_data.repository.signal.SignalRepositoryImpl
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalCacheDataSource
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalLocalDataSource
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalRemoteDataSource
import com.clonect.feeltalk.new_data.repository.token.TokenRepositoryImpl
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenLocalDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_domain.repository.partner.PartnerRepository
import com.clonect.feeltalk.new_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideAccountRepository(
        cacheDataSource: AccountCacheDataSource,
        localDataSource: AccountLocalDataSource,
        remoteDataSource: AccountRemoteDataSource
    ): AccountRepository {
        return AccountRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource)
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
        pagingSource: ChatPagingSource,
    ): ChatRepository {
        return ChatRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource, pagingSource)
    }

    @Singleton
    @Provides
    fun providesQuestionRepository(
        remoteDataSource: QuestionRemoteDataSource,
        localDataSource: QuestionLocalDataSource,
        cacheDataSource: QuestionCacheDataSource,
        pagingSource: QuestionPagingSource,
    ): QuestionRepository {
        return QuestionRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource, pagingSource)
    }

    @Singleton
    @Provides
    fun providesChallengeRepository(
        remoteDataSource: ChallengeRemoteDataSource,
        localDataSource: ChallengeLocalDataSource,
        cacheDataSource: ChallengeCacheDataSource,
        ongoingPagingSource: OngoingChallengePagingSource,
        completedPagingSource: CompletedChallengePagingSource
    ): ChallengeRepository {
        return ChallengeRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource, ongoingPagingSource, completedPagingSource)
    }

    @Singleton
    @Provides
    fun providesPartnerRepository(
        remoteDataSource: PartnerRemoteDataSource,
        localDataSource: PartnerLocalDataSource,
        cacheDataSource: PartnerCacheDataSource,
    ): PartnerRepository {
        return PartnerRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource)
    }

    @Singleton
    @Provides
    fun providesSignalRepository(
        remoteDataSource: SignalRemoteDataSource,
        localDataSource: SignalLocalDataSource,
        cacheDataSource: SignalCacheDataSource,
    ): SignalRepository {
        return SignalRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource)
    }

    @Singleton
    @Provides
    fun providesMixpanelRepository(
        remoteDataSource: MixpanelRemoteDataSource,
        localDataSource: MixpanelLocalDataSource,
        cacheDataSource: MixpanelCacheDataSource,
    ): MixpanelRepository {
        return MixpanelRepositoryImpl(cacheDataSource, localDataSource, remoteDataSource)
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
    fun providesQuestionRepository2(
        remoteDataSource: QuestionRemoteDataSource2,
        localDataSource: QuestionLocalDataSource2,
        cacheDataSource: QuestionCacheDataSource2,
        userLevelEncryptHelper: UserLevelEncryptHelper
    ): QuestionRepository2 {
        return QuestionRepository2Impl(remoteDataSource, localDataSource, cacheDataSource, userLevelEncryptHelper)
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