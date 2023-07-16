package com.clonect.feeltalk.new_presentation.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.AutoLogInUseCase
import com.clonect.feeltalk.new_domain.model.appSettings.AppSettings
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val autoLogInUseCase: AutoLogInUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isUser = MutableStateFlow(false)
    val isUser = _isUser.asStateFlow()

    private val _isUserCouple = MutableStateFlow(false)
    val isUserCouple = _isUserCouple.asStateFlow()

    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()

    init {

    }

    suspend fun autoLogIn() = withContext(Dispatchers.IO) {
        when (val result = autoLogInUseCase()) {
            is Resource.Success -> {
                when (result.data.signUpState) {
                    "newbie" -> {
                        _isUser.value = false
                    }
                    "solo" -> {
                        _isUser.value = true
                        _isUserCouple.value = false
                    }
                    "couple" -> {
                        _isUser.value = true
                        _isUserCouple.value = true
                    }
                }
                _isLoggedIn.value = true
                infoLog("Success to auto log in")
            }
            is Resource.Error -> {
                _isLoggedIn.value = false
                result.throwable.printStackTrace()
                infoLog("Fail to auto log in: ${result.throwable.stackTrace.joinToString("\n")}")
                setReady()
            }
        }
    }


    fun enablePushNotificationEnabled(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
//                if (enabled) {
//                    subscribeToTopic(Topics.Push.text)
//                }
//                else {
//                    unsubscribeFromTopic(Topics.Push.text)
//                }
                val appSettings = getAppSettingsUseCase()
                appSettings.fcmToken = it
                appSettings.isPushNotificationEnabled = enabled
                saveAppSettings(appSettings)
            }
        }
    }

    private fun saveAppSettings(appSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(appSettings)
    }


    fun setReady() {
        _isReady.value = true
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toast.emit(message)
    }


    private fun logInMixpanel(userInfo: UserInfo) {
        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.email, true)
    }
}