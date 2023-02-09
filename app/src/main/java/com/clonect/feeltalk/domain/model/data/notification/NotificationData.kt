package com.clonect.feeltalk.domain.model.data.notification

data class NotificationData(
    val title: String,
    val message: String,
    val type: String = ""
)
