package com.clonect.feeltalk.new_presentation.ui.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.GetCoupleCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.ReLogInUseCase
import com.clonect.feeltalk.new_domain.usecase.token.CacheSocialTokenUseCase
import com.clonect.feeltalk.presentation.utils.AppSettings
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
class SignUpViewModel @Inject constructor(
    private val reLogInUseCase: ReLogInUseCase,
    private val cacheSocialTokenUseCase: CacheSocialTokenUseCase,
    private val getCoupleCodeUseCase: GetCoupleCodeUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
): ViewModel() {

    var appSettings = getAppSettingsUseCase()

    private val _navigateToAgreement = MutableSharedFlow<Boolean>()
    val navigateToAgreement = _navigateToAgreement.asSharedFlow()

    private val _navigateToCoupleCode = MutableSharedFlow<Boolean>()
    val navigateToCoupleCode = _navigateToCoupleCode.asSharedFlow()

    private val _navigateToMain = MutableSharedFlow<Boolean>()
    val navigateToMain = _navigateToMain.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    // login
    suspend fun reLogIn(socialToken: SocialToken) = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        when (val result = reLogInUseCase(socialToken)) {
            is Resource.Success -> {
                setLoading(false)
                when (result.data.lowercase()) {
                    "newbie" -> {
                        cacheSocialToken(socialToken)
                        _navigateToAgreement.emit(true)
                    }
                    "solo" -> {
                        getCoupleCode()
                        _navigateToCoupleCode.emit(true)
                    }
                    "couple" -> {
                        _navigateToMain.emit(true)
                    }
                }
            }
            is Resource.Error -> {
                setLoading(false)
                infoLog("재 로그인 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { _errorMessage.emit(it) }
            }
        }
    }

    private suspend fun cacheSocialToken(socialToken: SocialToken) = withContext(Dispatchers.IO) {
        when (val result = cacheSocialTokenUseCase(socialToken)) {
            is Resource.Success -> {
                result.data
            }
            is Resource.Error -> {
                throw result.throwable
            }
        }
    }

    private suspend fun getCoupleCode() = withContext(Dispatchers.IO) {
        val result = getCoupleCodeUseCase()
        if (result is Resource.Error) {
            infoLog("커플코드 로딩 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            result.throwable.localizedMessage?.let { sendErrorMessage(it) }
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
                appSettings.fcmToken = it
                appSettings.isPushNotificationEnabled = enabled
                saveAppSettings(appSettings)
            }
        }
    }

    private fun saveAppSettings(newAppSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(newAppSettings)
    }


    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


//    private fun logInMixpanel(userInfo: UserInfo) {
//        val mixpanel = getMixpanelAPIUseCase()
//        mixpanel.identify(userInfo.email, true)
//        mixpanel.registerSuperProperties(JSONObject().apply {
//            put("gender", userInfo.gender)
//        })
//    }


}