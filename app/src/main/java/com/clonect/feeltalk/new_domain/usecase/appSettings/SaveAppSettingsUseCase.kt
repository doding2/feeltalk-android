package com.clonect.feeltalk.new_domain.usecase.appSettings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.model.appSettings.AppSettings

class SaveAppSettingsUseCase(
    private val settingsPref: SharedPreferences,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
) {
    operator fun invoke(appSettings: AppSettings) {
        settingsPref.edit {
            appSettings.run {
                putBoolean("isAppSettingsNotChanged", false)
                putBoolean("isPushNotificationEnabled", isPushNotificationEnabled)
                putInt("chatNotificationStack", chatNotificationStack)
                putString("fcmToken", appSettings.fcmToken?.let { appLevelEncryptHelper.encrypt("fcmToken", it) })
            }
        }
    }
}