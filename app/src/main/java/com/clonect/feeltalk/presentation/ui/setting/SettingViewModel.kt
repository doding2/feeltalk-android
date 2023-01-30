package com.clonect.feeltalk.presentation.ui.setting

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.domain.model.notification.Topics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SettingViewModel @Inject constructor(
    @Named("AppSettings")
    private val pref: SharedPreferences
): ViewModel() {

    private var _isPushNotificationEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(pref.getBoolean("isPushNotificationEnabled", false))
    val isPushNotificationEnabled = _isPushNotificationEnabled.asStateFlow()

    private var _isUsageInfoNotificationEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(pref.getBoolean("isUsageInfoNotificationEnabled", false))
    val isUsageInfoNotificationEnabled = _isUsageInfoNotificationEnabled.asStateFlow()


    init {

    }


    fun enablePushNotification(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                if (enabled) {
                    subscribeToTopic(Topics.Push.text)
                } else {
                    unsubscribeFromTopic(Topics.Push.text)
                }
                pref.edit()
                    .putBoolean("isPushNotificationEnabled", enabled)
                    .apply()
                _isPushNotificationEnabled.value = enabled
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
                pref.edit()
                    .putBoolean("isUsageInfoNotificationEnabled", enabled)
                    .apply()
                _isUsageInfoNotificationEnabled.value = enabled
            }
        }
    }


}