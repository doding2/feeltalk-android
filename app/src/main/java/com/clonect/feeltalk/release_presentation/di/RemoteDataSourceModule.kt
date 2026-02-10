package com.clonect.feeltalk.release_presentation.di

import android.content.Context
import com.clonect.feeltalk.mvp_data.repository.chat.datasource.ChatRemoteDataSource2
import com.clonect.feeltalk.mvp_data.repository.chat.datasourceImpl.ChatRemoteDataSource2Impl
import com.clonect.feeltalk.mvp_data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.mvp_data.repository.encryption.datasourceImpl.EncryptionRemoteDataSourceImpl
import com.clonect.feeltalk.mvp_data.repository.question.datasource.QuestionRemoteDataSource2
import com.clonect.feeltalk.mvp_data.repository.question.datasourceImpl.QuestionRemoteDataSource2Impl
import com.clonect.feeltalk.mvp_data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.mvp_data.repository.user.datasourceImpl.UserRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.api.ClonectService
import com.clonect.feeltalk.release_data.repository.account.dataSource.AccountRemoteDataSource
import com.clonect.feeltalk.release_data.repository.account.dataSourceImpl.AccountRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.release_data.repository.challenge.dataSourceImpl.ChallengeRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.release_data.repository.chat.dataSourceImpl.ChatRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.mixpanel.dataSource.MixpanelRemoteDataSource
import com.clonect.feeltalk.release_data.repository.mixpanel.dataSourceImpl.MixpanelRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.partner.dataSource.PartnerRemoteDataSource
import com.clonect.feeltalk.release_data.repository.partner.dataSourceImpl.PartnerRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.release_data.repository.question.dataSourceImpl.QuestionRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.signal.dataSource.SignalRemoteDataSource
import com.clonect.feeltalk.release_data.repository.signal.dataSourceImpl.SignalRemoteDataSourceImpl
import com.clonect.feeltalk.release_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.release_data.repository.token.dataSourceImpl.TokenRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataSourceModule {

    @Singleton
    @Provides
    fun providesAccountRemoteDatasource(clonectService: ClonectService): AccountRemoteDataSource {
        return AccountRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesTokenRemoteDataSource(clonectService: ClonectService): TokenRemoteDataSource {
        return TokenRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesChatRemoteDataSource(
        @ApplicationContext context: Context,
        clonectService: ClonectService
    ): ChatRemoteDataSource {
        return ChatRemoteDataSourceImpl(context, clonectService)
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

    @Singleton
    @Provides
    fun providesPartnerRemoteDataSource(
        clonectService: ClonectService
    ): PartnerRemoteDataSource {
        return PartnerRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesSignalRemoteDataSource(
        clonectService: ClonectService
    ): SignalRemoteDataSource {
        return SignalRemoteDataSourceImpl(clonectService)
    }

    @Singleton
    @Provides
    fun providesMixpanelRemoteDataSource(
        clonectService: ClonectService
    ): MixpanelRemoteDataSource {
        return MixpanelRemoteDataSourceImpl(clonectService)
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