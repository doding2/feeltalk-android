package com.clonect.feeltalk.presentation.di

import android.content.Context
import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.db.FeeltalkDatabase
import com.clonect.feeltalk.data.db.QuestionDao
import com.clonect.feeltalk.data.repository.chat.datasource.ChatLocalDataSource
import com.clonect.feeltalk.data.repository.chat.datasourceImpl.ChatLocalDataSourceImpl
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasourceImpl.EncryptionLocalDataSourceImpl
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource
import com.clonect.feeltalk.data.repository.question.datasourceImpl.QuestionLocalDataSourceImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserLocalDataSourceImpl
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
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
    fun providesChatLocalDataSource(
        chatDao: ChatDao
    ): ChatLocalDataSource {
        return ChatLocalDataSourceImpl(chatDao)
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
        feeltalkDatabase: FeeltalkDatabase,
    ): UserLocalDataSource {
        return UserLocalDataSourceImpl(context, appLevelEncryptHelper, feeltalkDatabase)
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