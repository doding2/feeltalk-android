package com.clonect.feeltalk.presentation.di

import android.content.SharedPreferences
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Provides
    @Singleton
    fun providesAppLevelEncryptionHelper(@Named("AppLevelEncryption") pref: SharedPreferences): AppLevelEncryptHelper {
        return AppLevelEncryptHelper(pref)
    }

    @Provides
    @Singleton
    fun providesMessageEncryptHelper(): MessageEncryptHelper {
        return MessageEncryptHelper()
    }

}