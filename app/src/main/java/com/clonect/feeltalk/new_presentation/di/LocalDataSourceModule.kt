package com.clonect.feeltalk.new_presentation.di

import android.content.Context
import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.db.QuestionDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource2
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatLocalDataSource2Impl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionLocalDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionLocalDataSourceImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserLocalDataSourceImpl
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatLocalDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl.ChatLocalDataSourceImpl
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInLocalDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSourceImpl.SignInLocalDataSourceImpl
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
    fun providesSignInLocalDatasource(): SignInLocalDataSource {
        return SignInLocalDataSourceImpl()
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
    fun providesQuestionLocalDataSource(
        questionDao: QuestionDao
    ): QuestionLocalDataSource {
        return QuestionLocalDataSourceImpl(questionDao)
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