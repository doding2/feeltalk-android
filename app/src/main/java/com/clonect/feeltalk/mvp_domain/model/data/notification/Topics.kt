package com.clonect.feeltalk.mvp_domain.model.data.notification

sealed class Topics(val text: String) {
    object Push: Topics("Push")
    object UsageInfo: Topics("UsageInfo")
}
