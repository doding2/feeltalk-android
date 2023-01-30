package com.clonect.feeltalk.domain.model.notification

sealed class Topics(val text: String) {
    object Push: Topics("Push")
    object UsageInfo: Topics("UsageInfo")
}
