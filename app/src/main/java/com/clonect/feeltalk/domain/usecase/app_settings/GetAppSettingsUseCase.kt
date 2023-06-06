package com.clonect.feeltalk.domain.usecase.app_settings

import android.content.SharedPreferences
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.presentation.utils.AppSettings

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
                isUsageInfoNotificationEnabled = getBoolean(
                    "isUsageInfoNotificationEnabled",
                    false,
                ),
                isNotificationUpdated = getBoolean(
                    "isNotificationUpdated",
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