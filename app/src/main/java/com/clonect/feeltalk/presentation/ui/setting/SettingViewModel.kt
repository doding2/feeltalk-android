package com.clonect.feeltalk.presentation.ui.setting

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.notification.Topics
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
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
    @Named("AppSettings") private val pref: SharedPreferences,
    private val getUserInfoUseCase: GetUserInfoUseCase,
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private var _isPushNotificationEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(pref.getBoolean("isPushNotificationEnabled", false))
    val isPushNotificationEnabled = _isPushNotificationEnabled.asStateFlow()

    private var _isUsageInfoNotificationEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(pref.getBoolean("isUsageInfoNotificationEnabled", false))
    val isUsageInfoNotificationEnabled = _isUsageInfoNotificationEnabled.asStateFlow()


    init {
        getUserInfo()
    }

    private fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = getUserInfoUseCase()) {
            is Resource.Success -> _userInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
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