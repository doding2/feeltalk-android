package com.clonect.feeltalk.presentation.ui.setting

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _isPushNotificationEnabled = MutableStateFlow(false)
    val isPushNotificationEnabled = _isPushNotificationEnabled.asStateFlow()

    private val _isUsageInfoNotificationEnabled = MutableStateFlow(false)
    val isUsageInfoNotificationEnabled = _isUsageInfoNotificationEnabled.asStateFlow()

    init {
        getAppSettings()
    }

    // TODO 토픽 구독 해제도 추가
    fun enablePushNotification(enabled: Boolean) {
        if (_isPushNotificationEnabled.value == enabled)
            return
        pref.edit()
            .putBoolean("isPushNotificationEnabled", enabled)
            .apply()
    }

    // TODO 토픽 구독 해제도 추가
    fun enableUsageInfoNotification(enabled: Boolean) {
        if (_isUsageInfoNotificationEnabled.value == enabled)
            return
        pref.edit()
            .putBoolean("isUsageInfoNotificationEnabled", enabled)
            .apply()
    }


    private fun getAppSettings() = viewModelScope.launch(Dispatchers.IO) {
        pref.apply {
            _isPushNotificationEnabled.value = getBoolean("isPushNotificationEnabled", false)
            _isUsageInfoNotificationEnabled.value = getBoolean("isUsageInfoNotificationEnabled", false)
        }
    }
}