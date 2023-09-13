package com.clonect.feeltalk.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.notification.Topics
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.new_domain.model.appSettings.AppSettings
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class Setting2ViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCoupleAnniversaryUseCase: GetCoupleAnniversaryUseCase,
    private val clearAllExceptKeysUseCase: ClearAllExceptKeysUseCase,
    private val getMyProfileImageUrlUseCase: GetMyProfileImageUrlUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
): ViewModel() {

    private val appSettings = getAppSettingsUseCase()

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _coupleAnniversary = MutableStateFlow<String?>(null)
    val coupleAnniversary = _coupleAnniversary.asStateFlow()

    private val _myProfileImageUrl = MutableStateFlow<String?>(null)
    val myProfileImageUrl = _myProfileImageUrl.asStateFlow()


    private var _isPushNotificationEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(appSettings.isPushNotificationEnabled)
    val isPushNotificationEnabled = _isPushNotificationEnabled.asStateFlow()

    private var _isUsageInfoNotificationEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isUsageInfoNotificationEnabled = _isUsageInfoNotificationEnabled.asStateFlow()


    fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = getUserInfoUseCase()) {
            is Resource.Success -> _userInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
    }

    fun getCoupleAnniversary() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleAnniversaryUseCase()
        when (result) {
            is Resource.Success -> { _coupleAnniversary.value = result.data }
            is Resource.Error -> infoLog("Fail to get d day: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner d day")
        }
    }

    fun getMyProfileImageUrl() = viewModelScope.launch(Dispatchers.IO) {
        val result = getMyProfileImageUrlUseCase()
        when (result) {
            is Resource.Success -> { _myProfileImageUrl.value = result.data }
            is Resource.Error -> infoLog("Fail to get my profile image url: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get my profile image url")
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
                appSettings.fcmToken = it
                appSettings.isPushNotificationEnabled = enabled
                saveAppSettings(appSettings)
                _isPushNotificationEnabled.value = enabled
                enablePushNotificationMixpanel(enabled)
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
                appSettings.fcmToken = it
                saveAppSettings(appSettings)
                _isUsageInfoNotificationEnabled.value = enabled
            }
        }
    }

    fun saveAppSettings(appSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(appSettings)
    }



    private fun enablePushNotificationMixpanel(enabled: Boolean) = CoroutineScope(Dispatchers.IO).launch { 
        val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.data.email, true)
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("gender", userInfo.data.gender)
        })

        mixpanel.track("Enable Push Notification")
        mixpanel.people.set("isPushNotificationEnabled", enabled)
    }


    suspend fun clearAllExceptKeys(): Boolean {
        val clearResult = clearAllExceptKeysUseCase()
        return (clearResult is Resource.Success)
    }
}