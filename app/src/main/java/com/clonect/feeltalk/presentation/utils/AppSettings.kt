package com.clonect.feeltalk.presentation.utils

import java.io.Serializable

data class AppSettings(
    var isAppSettingsNotChanged: Boolean = true,
    var isPushNotificationEnabled: Boolean = false,
    var isUsageInfoNotificationEnabled: Boolean = false,
    var isNotificationUpdated: Boolean = false,
    var fcmToken: String? = null
): Serializable
