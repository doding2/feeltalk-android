package com.clonect.feeltalk.release_domain.usecase.appSettings

import android.content.SharedPreferences
import com.clonect.feeltalk.release_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.release_domain.model.appSettings.AppSettings

class GetAppSettingsUseCase(
    private val settingsPref: SharedPreferences,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
) {
    operator fun invoke(): AppSettings {
        return settingsPref.run {
            AppSettings(
                isAppSettingsNotChanged = getBoolean(
                    "isAppSettingsNotChanged",
                    true
                ),
                isPushNotificationEnabled = getBoolean(
                    "isPushNotificationEnabled",
                    false
                ),
                fcmToken = getString(
                    "fcmToken",
                    null,
                )?.let { try { appLevelEncryptHelper.decrypt("fcmToken", it) } catch (e: Exception) { null } }
            )
        }
    }
}