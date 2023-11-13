package com.clonect.feeltalk.new_presentation.di

import android.content.SharedPreferences
import com.clonect.feeltalk.data.utils.DatabaseEncryptHelper
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper
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
    fun providesNotificationHelper(
        getAppSettingsUseCase: GetAppSettingsUseCase,
        saveAppSettingsUseCase: SaveAppSettingsUseCase
    ): NotificationHelper {
        return NotificationHelper(getAppSettingsUseCase, saveAppSettingsUseCase)
    }






    /** Old **/

    @Provides
    @Singleton
    fun providesMessageEncryptHelper(): MessageEncryptHelper {
        return MessageEncryptHelper()
    }

    @Provides
    @Singleton
    fun providesUserLevelEncryptHelper(encryptRepository: EncryptionRepository): UserLevelEncryptHelper {
        return UserLevelEncryptHelper(encryptRepository)
    }

    @Provides
    @Singleton
    fun providesDatabaseEncryptHelper(): DatabaseEncryptHelper {
        return DatabaseEncryptHelper()
    }

}