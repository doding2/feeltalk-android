package com.clonect.feeltalk.domain.usecase.app_settings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.domain.repository.UserRepository
import com.clonect.feeltalk.presentation.utils.AppSettings
import com.clonect.feeltalk.presentation.utils.infoLog

class SaveAppSettingsUseCase(
    private val settingsPref: SharedPreferences,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(appSettings: AppSettings) {
        val prefFcmToken = settingsPref.getString("fcmToken", null)
            ?.let { appLevelEncryptHelper.decrypt("fcmToken", it)  }
        if (prefFcmToken != appSettings.fcmToken) {
            appSettings.fcmToken?.let {
                infoLog("update fcm token: ${it}")
                val result = userRepository.updateFcmToken(it)
                if (result !is Resource.Success) {
                    appSettings.fcmToken = null
                }
            }
        }

        settingsPref.edit {
            appSettings.run {
                putBoolean("isAppSettingsNotChanged", false)
                putBoolean("isPushNotificationEnabled", isPushNotificationEnabled)
                putBoolean("isUsageInfoNotificationEnabled", isUsageInfoNotificationEnabled)
                putString("fcmToken", appSettings.fcmToken?.let { appLevelEncryptHelper.encrypt("fcmToken", it) })
            }
        }
    }
}