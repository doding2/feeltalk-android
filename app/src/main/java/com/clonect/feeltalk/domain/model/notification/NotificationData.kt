package com.clonect.feeltalk.domain.model.notification

data class NotificationData(
    val title: String,
    val message: String,
    val type: String = ""
)
