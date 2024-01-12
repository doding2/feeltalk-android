package com.clonect.feeltalk.new_presentation.ui.signUp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.usecase.account.CheckAccountLockedUseCase
import com.clonect.feeltalk.new_domain.usecase.account.GetMyInfoUseCase
import com.clonect.feeltalk.new_domain.usecase.account.ReLogInUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.GetUserStatusNewUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.LogInAppleUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.LogInNewUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.token.CacheSocialTokenUseCase
import com.clonect.feeltalk.new_domain.usecase.token.UpdateFcmTokenUseCase
import com.clonect.feeltalk.new_presentation.service.FirebaseCloudMessagingService
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val reLogInUseCase: ReLogInUseCase,
    private val cacheSocialTokenUseCase: CacheSocialTokenUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
    private val checkAccountLockedUseCase: CheckAccountLockedUseCase,
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,

    private val logInNewUseCase: LogInNewUseCase,
    private val logInAppleUseCase: LogInAppleUseCase,
    private val getUserStatusNewUseCase: GetUserStatusNewUseCase,
): ViewModel() {

    private val _navigateToAgreement = MutableStateFlow(false)
    val navigateToAgreement = _navigateToAgreement.asStateFlow()

    private val _navigateToCoupleCode = MutableStateFlow(false)
    val navigateToCoupleCode = _navigateToCoupleCode.asStateFlow()

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain = _navigateToMain.asStateFlow()

    private val _navigateToPassword = MutableStateFlow(false)
    val navigateToPassword = _navigateToPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val defaultErrorMessage = context.getString(R.string.pillowtalk_default_error_message)

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    suspend fun logInNew(socialToken: SocialToken) = viewModelScope.launch {
        setLoading(true)

        val isAppleAuth = socialToken.type == SocialType.AppleAndroid && socialToken.state != null
        val logInResult = if (isAppleAuth) {
            logInAppleUseCase(
                state = socialToken.state!!
            )
        } else {
            logInNewUseCase(
                oauthId = socialToken.oauthId ?: return@launch,
                snsType = socialToken.type
            )
        }
        if (logInResult is Resource.Error) {
            infoLog("Fail to log in new: ${logInResult.throwable.localizedMessage}")
            sendErrorMessage(defaultErrorMessage)
            setLoading(false)
            return@launch
        }

        getUserStatusNewUseCase()
            .onError { infoLog("Fail to get user status new: ${it.localizedMessage}") }
            .onSuccess {
                when (it.memberStatus.lowercase()) {
                    "newbie" -> {
                        FirebaseCloudMessagingService.clearFcmToken()
                        cacheSocialToken(socialToken)
                        _navigateToAgreement.value = true
                    }
                    "solo" -> {
                        updateFcmToken()
                        _navigateToCoupleCode.value = true
                    }
                    "couple" -> {
                        updateFcmToken()
                        val isLocked = preloadCoupleData()
                        if (isLocked) {
                            _navigateToPassword.value = true
                        } else {
                            _navigateToMain.value = true
                        }
                    }
                }
            }


        setLoading(false)
    }

    // login
    suspend fun reLogIn(socialToken: SocialToken) = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        when (val result = reLogInUseCase(socialToken)) {
            is Resource.Success -> {
                when (result.data.lowercase()) {
                    "newbie" -> {
                        FirebaseCloudMessagingService.clearFcmToken()
                        cacheSocialToken(socialToken)
                        _navigateToAgreement.value = true
                    }
                    "solo" -> {
                        updateFcmToken()
                        _navigateToCoupleCode.value = true
                    }
                    "couple" -> {
                        updateFcmToken()
                        val isLocked = preloadCoupleData()
                        if (isLocked) {
                            _navigateToPassword.value = true
                        } else {
                            _navigateToMain.value = true
                        }
                    }
                }
            }
            is Resource.Error -> {
                setLoading(false)
                infoLog("재 로그인 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                sendErrorMessage(defaultErrorMessage)
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
    private suspend fun preloadCoupleData() = withContext(Dispatchers.IO) {
        val isAccountLocked = async {
            when (val result = checkAccountLockedUseCase()) {
                is Resource.Success -> {
                    infoLog("isAccountLocked: ${result.data}")
                    result.data
                }
                is Resource.Error -> {
                    result.throwable.printStackTrace()
                    infoLog("Fail to check account locked: ${result.throwable}\n${result.throwable.stackTrace.joinToString("\n")}")
                    false
                }
            }
        }

        val todayQuestion = async {
            when (val result = getTodayQuestionUseCase()) {
                is Resource.Success -> { }
                is Resource.Error -> {
                    infoLog("Fail to preload today question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
                }
            }
        }

        todayQuestion.await()
        isAccountLocked.await()
    }
    
    private suspend fun updateFcmToken() = withContext(Dispatchers.IO) {
        FirebaseCloudMessagingService.clearFcmToken()

        val fcmToken = FirebaseCloudMessagingService.getFcmToken() ?: run {
            infoLog("fcmToken is null.")
            throw NullPointerException("FcmToken is null.")
        }

        when (val result = updateFcmTokenUseCase(fcmToken)) {
            is Resource.Success -> {  }
            is Resource.Error -> {
                infoLog("fcmToken 업데이트 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                throw result.throwable
            }
        }
    }


    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


}