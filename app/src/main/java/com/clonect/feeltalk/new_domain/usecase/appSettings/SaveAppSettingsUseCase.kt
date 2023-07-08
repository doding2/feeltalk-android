package com.clonect.feeltalk.new_domain.usecase.appSettings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.presentation.utils.AppSettings

class SaveAppSettingsUseCase(
    private val settingsPref: SharedPreferences,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
) {
    suspend operator fun invoke(appSettings: AppSettings) {
        val prefFcmToken = settingsPref.getString("fcmToken", null)
            ?.let { appLevelEncryptHelper.decrypt("fcmToken", it)  }
//        if (prefFcmToken != appSettings.fcmToken) {
//            appSettings.fcmToken?.let {
//                val result = userRepository.updateFcmToken(it)
//                if (result is Resource.Success) {
//                    infoLog("update fcm token: $it")
//                } else {
//                    appSettings.fcmToken = null
//                }
//            }
//        }

        settingsPref.edit {
            appSettings.run {
                putBoolean("isAppSettingsNotChanged", false)
                putBoolean("isPushNotificationEnabled", isPushNotificationEnabled)
                putBoolean("isUsageInfoNotificationEnabled", isUsageInfoNotificationEnabled)
                putBoolean("isNotificationUpdated", isNotificationUpdated)
                putString("fcmToken", appSettings.fcmToken?.let { appLevelEncryptHelper.encrypt("fcmToken", it) })
            }
        }
    }
}