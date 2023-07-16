package com.clonect.feeltalk.new_domain.model.appSettings

import java.io.Serializable

data class AppSettings(
    var isAppSettingsNotChanged: Boolean = true,
    var isPushNotificationEnabled: Boolean = false,
    var activeChatNotification: Int = 0,
    var fcmToken: String? = null
): Serializable
