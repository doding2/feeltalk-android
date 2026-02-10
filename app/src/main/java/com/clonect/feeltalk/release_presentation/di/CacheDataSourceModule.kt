package com.clonect.feeltalk.release_presentation.di

import android.content.Context
import com.clonect.feeltalk.mvp_data.repository.chat.datasource.ChatCacheDataSource2
import com.clonect.feeltalk.mvp_data.repository.chat.datasourceImpl.ChatCacheDataSource2Impl
import com.clonect.feeltalk.mvp_data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.mvp_data.repository.encryption.datasourceImpl.EncryptionCacheDataSourceImpl
import com.clonect.feeltalk.mvp_data.repository.question.datasource.QuestionCacheDataSource2
import com.clonect.feeltalk.mvp_data.repository.question.datasourceImpl.QuestionCacheDataSource2Impl
import com.clonect.feeltalk.mvp_data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.mvp_data.repository.user.datasourceImpl.UserCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.account.dataSource.AccountCacheDataSource
import com.clonect.feeltalk.release_data.repository.account.dataSourceImpl.AccountCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeCacheDataSource
import com.clonect.feeltalk.release_data.repository.challenge.dataSourceImpl.ChallengeCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.release_data.repository.chat.dataSourceImpl.ChatCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.mixpanel.dataSource.MixpanelCacheDataSource
import com.clonect.feeltalk.release_data.repository.mixpanel.dataSourceImpl.MixpanelCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.partner.dataSource.PartnerCacheDataSource
import com.clonect.feeltalk.release_data.repository.partner.dataSourceImpl.PartnerCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.question.dataSource.QuestionCacheDataSource
import com.clonect.feeltalk.release_data.repository.question.dataSourceImpl.QuestionCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.signal.dataSource.SignalCacheDataSource
import com.clonect.feeltalk.release_data.repository.signal.dataSourceImpl.SignalCacheDataSourceImpl
import com.clonect.feeltalk.release_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.release_data.repository.token.dataSourceImpl.TokenCacheDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheDataSourceModule {

    @Singleton
    @Provides
    fun providesAccountCacheDatasource(): AccountCacheDataSource {
        return AccountCacheDataSourceImpl()
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

    @Singleton
    @Provides
    fun providesChallengeCacheDataSource(): ChallengeCacheDataSource {
        return ChallengeCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesPartnerCacheDataSource(): PartnerCacheDataSource {
        return PartnerCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesSignalCacheDataSource(): SignalCacheDataSource {
        return SignalCacheDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesMixpanelCacheDataSource(
        @ApplicationContext context: Context
    ): MixpanelCacheDataSource {
        return MixpanelCacheDataSourceImpl(context)
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