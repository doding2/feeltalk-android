package com.clonect.feeltalk.domain.usecase.app_settings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.presentation.utils.AppSettings

class SaveAppSettingsUseCase(
    private val settingsPref: SharedPreferences,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
) {
    operator fun invoke(appSettings: AppSettings) {
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