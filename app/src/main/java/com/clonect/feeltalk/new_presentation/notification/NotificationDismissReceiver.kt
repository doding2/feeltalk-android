package com.clonect.feeltalk.new_presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDismissReceiver: BroadcastReceiver() {

    @Inject
    lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    @Inject
    lateinit var saveAppSettingsUseCase: SaveAppSettingsUseCase

    override fun onReceive(context: Context, intent: Intent?) {
        val appSettings = getAppSettingsUseCase()
        appSettings.chatNotificationStack = 0
        saveAppSettingsUseCase(appSettings)
        infoLog("Notifications are dismissed")
    }
}