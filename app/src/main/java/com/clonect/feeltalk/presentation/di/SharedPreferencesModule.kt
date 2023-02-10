package com.clonect.feeltalk.presentation.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferencesModule {

    @Singleton
    @Provides
    @Named("AppSettings")
    fun providesAppSettingsPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("AppSettingsSharedPreferences", AppCompatActivity.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    @Named("AppLevelEncryption")
    fun providesAppLevelEncryptionPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("AppLevelEncryptionSharedPreferences", AppCompatActivity.MODE_PRIVATE)
    }

}