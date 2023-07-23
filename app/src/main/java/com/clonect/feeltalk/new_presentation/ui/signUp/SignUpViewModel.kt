package com.clonect.feeltalk.new_presentation.ui.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.usecase.signIn.ReLogInUseCase
import com.clonect.feeltalk.new_domain.usecase.token.CacheSocialTokenUseCase
import com.clonect.feeltalk.new_domain.usecase.token.UpdateFcmTokenUseCase
import com.clonect.feeltalk.new_presentation.service.FirebaseCloudMessagingService
import com.clonect.feeltalk.presentation.utils.infoLog
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
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
): ViewModel() {

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
                        updateFcmToken()
                        _navigateToCoupleCode.emit(true)
                    }
                    "couple" -> {
                        updateFcmToken()
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
    
    private suspend fun updateFcmToken() = withContext(Dispatchers.IO) {
        val fcmToken = FirebaseCloudMessagingService.getFcmToken() ?: run {
            infoLog("fcmToken is null.")
            _errorMessage.emit("잠시 후 다시 시도해주세요.")
            return@withContext
        }
        
        when (val result = updateFcmTokenUseCase(fcmToken)) {
            is Resource.Success -> {  }
            is Resource.Error -> {
                infoLog("fcmToken 업데이트 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { _errorMessage.emit(it) }
            }
        }
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