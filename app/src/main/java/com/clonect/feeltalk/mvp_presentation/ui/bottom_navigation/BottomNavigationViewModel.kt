package com.clonect.feeltalk.mvp_presentation.ui.bottom_navigation

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.notification.Topics
import com.clonect.feeltalk.mvp_domain.usecase.encryption.CheckKeyPairsExistUseCase
import com.clonect.feeltalk.mvp_domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.mvp_domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.release_domain.model.appSettings.AppSettings
import com.clonect.feeltalk.release_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.release_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
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
class BottomNavigationViewModel @Inject constructor(
    private val checkKeyPairsExistUseCase: CheckKeyPairsExistUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
): ViewModel() {

    var appSettings = getAppSettingsUseCase()

    private val _questionListScrollState = MutableStateFlow<Parcelable?>(null)
    val questionListScrollState = _questionListScrollState.asStateFlow()

    private val _homeScrollState = MutableStateFlow<Int?>(null)
    val homeScrollState = _homeScrollState.asStateFlow()

    private val _settingScrollState = MutableStateFlow<Int?>(null)
    val settingScrollState = _settingScrollState.asStateFlow()

    private val _isKeyPairsExist = MutableStateFlow(true)
    val isKeyPairsExist = _isKeyPairsExist.asStateFlow()

    init {
        initFirebase()
        checkKeyPairsExist()
    }

    private fun initFirebase() {
        appSettings.run {
            enablePushNotificationEnabled(isPushNotificationEnabled)
        }
    }

    private fun checkKeyPairsExist() = viewModelScope.launch(Dispatchers.IO) {
        val result = checkKeyPairsExistUseCase()
        when (result) {
            is Resource.Success -> {
                _isKeyPairsExist.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to check key pairs exist: ${result.throwable.localizedMessage}")
                _isKeyPairsExist.value = false
            }
            else -> {
                infoLog("Fail to check key pairs exist")
                _isKeyPairsExist.value = false
            }
        }
    }

    fun disableKeyPairsExist() {
        _isKeyPairsExist.value = true
    }



    fun getAppSettingsNotChanged(): Boolean {
        return getAppSettingsUseCase().isAppSettingsNotChanged
    }

    fun enablePushNotificationEnabled(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                if (enabled) {
                    subscribeToTopic(Topics.Push.text)
                }
                else {
                    unsubscribeFromTopic(Topics.Push.text)
                }
                appSettings.fcmToken = it
                appSettings.isPushNotificationEnabled = enabled
                saveAppSettings(appSettings)
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
            }
        }
    }


    private fun saveAppSettings(appSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(appSettings)
    }


    fun setQuestionListScrollState(state: Parcelable?) {
        _questionListScrollState.value = state
    }

    fun setHomeScrollState(state: Int?) {
        _homeScrollState.value = state
    }

    fun setSettingScrollState(state: Int?) {
        _settingScrollState.value = state
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
}