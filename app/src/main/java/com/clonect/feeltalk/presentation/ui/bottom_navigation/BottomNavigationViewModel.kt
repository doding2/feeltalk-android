package com.clonect.feeltalk.presentation.ui.bottom_navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.domain.model.data.notification.Topics
import com.clonect.feeltalk.domain.usecase.app_settings.GetAppSettingsUseCase
import com.clonect.feeltalk.domain.usecase.app_settings.SaveAppSettingsUseCase
import com.clonect.feeltalk.presentation.utils.AppSettings
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase
): ViewModel() {

    var appSettings = getAppSettingsUseCase()

    init {
        initFirebase()
    }

    private fun initFirebase() {
        appSettings.run {
            enablePushNotificationEnabled(isPushNotificationEnabled)
            enableUsageInfoNotification(isUsageInfoNotificationEnabled)
        }
    }

    fun getAppSettingsNotChanged(): Boolean {
        return getAppSettingsUseCase().isAppSettingsNotChanged
    }

    fun enablePushNotificationEnabled(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                infoLog("fcmToken: $it")
                if (enabled) {
                    subscribeToTopic(Topics.Push.text)
                }
                else {
                    unsubscribeFromTopic(Topics.Push.text)
                }
                appSettings.isPushNotificationEnabled = enabled
                saveAppSettings(appSettings)
            }
        }
    }


    fun enableUsageInfoNotification(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                if (enabled) {
                    subscribeToTopic(Topics.UsageInfo.text)
                } else {
                    unsubscribeFromTopic(Topics.UsageInfo.text)
                }
                appSettings.isUsageInfoNotificationEnabled = enabled
                saveAppSettings(appSettings)
            }
        }
    }


    private fun saveAppSettings(appSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(appSettings)
    }

}