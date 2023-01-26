package com.clonect.feeltalk.domain.model.notification

data class PushNotification(
    val data: NotificationData,
    val to: String
)
