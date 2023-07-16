package com.clonect.feeltalk.new_domain.usecase.appSettings

import android.content.SharedPreferences
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.model.appSettings.AppSettings

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
                unreadChatNotifications = getLong("unreadChatNotifications", 0L),
                fcmToken = getString(
                    "fcmToken",
                    null,
                )?.let { try { appLevelEncryptHelper.decrypt("fcmToken", it) } catch (e: Exception) { null } }
            )
        }
    }
}