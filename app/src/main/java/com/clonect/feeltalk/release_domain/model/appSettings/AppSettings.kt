package com.clonect.feeltalk.release_domain.model.appSettings

import java.io.Serializable

data class AppSettings(
    var isAppSettingsNotChanged: Boolean = true,
    var isPushNotificationEnabled: Boolean = false,
    var fcmToken: String? = null
): Serializable
