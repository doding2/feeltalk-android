package com.clonect.feeltalk.presentation.utils.delegates

import androidx.fragment.app.Fragment

interface PostNotificationsPermission {
    var onPostNotificationGranted: (Boolean) -> Unit
    fun Fragment.checkPostNotificationsPermission()
}