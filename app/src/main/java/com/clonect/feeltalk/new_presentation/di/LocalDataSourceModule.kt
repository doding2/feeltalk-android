package com.clonect.feeltalk.new_presentation.di

import android.content.Context
import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.db.QuestionDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource2
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatLocalDataSource2Impl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionLocalDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource2
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionLocalDataSource2Impl
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserLocalDataSourceImpl
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountLocalDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSourceImpl.AccountLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeLocalDataSource
import com.clonect.feeltalk.new_data.repository.challenge.dataSourceImpl.ChallengeLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatLocalDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl.ChatLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelLocalDataSource
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSourceImpl.MixpanelLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.partner.dataSource.PartnerLocalDataSource
import com.clonect.feeltalk.new_data.repository.partner.dataSourceImpl.PartnerLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionLocalDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSourceImpl.QuestionLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalLocalDataSource
import com.clonect.feeltalk.new_data.repository.signal.dataSourceImpl.SignalLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenLocalDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSourceImpl.TokenLocalDataSourceImpl
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDataSourceModule {

    @Singleton
    @Provides
    fun providesAccountLocalDatasource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper): AccountLocalDataSource {
        return AccountLocalDataSourceImpl(context, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesTokenLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): TokenLocalDataSource {
        return TokenLocalDataSourceImpl(context, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesChatLocalDataSource(): ChatLocalDataSource {
        return ChatLocalDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesQuestionLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): QuestionLocalDataSource {
        return QuestionLocalDataSourceImpl(context, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesChallengeLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): ChallengeLocalDataSource {
        return ChallengeLocalDataSourceImpl(context, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesPartnerLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): PartnerLocalDataSource {
        return PartnerLocalDataSourceImpl(context, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesSignalLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): SignalLocalDataSource {
        return SignalLocalDataSourceImpl(context, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesMixpanelLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): MixpanelLocalDataSource {
        return MixpanelLocalDataSourceImpl(context, appLevelEncryptHelper)
    }




    /** Old **/

    @Singleton
    @Provides
    fun providesChatLocalDataSource2(
        chatDao: ChatDao
    ): ChatLocalDataSource2 {
        return ChatLocalDataSource2Impl(chatDao)
    }

    @Singleton
    @Provides
    fun providesQuestionLocalDataSource2(
        questionDao: QuestionDao
    ): QuestionLocalDataSource2 {
        return QuestionLocalDataSource2Impl(questionDao)
    }

    @Singleton
    @Provides
    fun providesUserLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper,
        chatDao: ChatDao,
        questionDao: QuestionDao
    ): UserLocalDataSource {
        return UserLocalDataSourceImpl(context, appLevelEncryptHelper, chatDao, questionDao)
    }

    @Singleton
    @Provides
    fun providesEncryptionLocalDataSource(
        @ApplicationContext context: Context,
        appLevelEncryptHelper: AppLevelEncryptHelper,
        messageEncryptHelper: MessageEncryptHelper,
    ): EncryptionLocalDataSource {
        return EncryptionLocalDataSourceImpl(context, appLevelEncryptHelper, messageEncryptHelper)
    }


}