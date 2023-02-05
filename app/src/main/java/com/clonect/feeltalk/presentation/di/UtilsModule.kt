package com.clonect.feeltalk.presentation.di

import android.content.Context
import android.content.SharedPreferences
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Provides
    @Singleton
    fun providesAppLevelEncryptionHelper(@ApplicationContext context: Context, @Named("AppLevelEncryption") pref: SharedPreferences): AppLevelEncryptHelper {
        return AppLevelEncryptHelper(context, pref)
    }

}