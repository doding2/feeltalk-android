package com.clonect.feeltalk.new_presentation.ui.signUpNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.GetCoupleCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.MatchCoupleUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.SignUpUseCase
import com.clonect.feeltalk.new_domain.usecase.token.GetCachedSocialTokenUseCase
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.CreateCoupleObserver
import com.clonect.feeltalk.new_presentation.service.FirebaseCloudMessagingService
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpNavigationViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val getCachedSocialTokenUseCase: GetCachedSocialTokenUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val getCoupleCodeUseCase: GetCoupleCodeUseCase,
    private val matchCoupleUseCase: MatchCoupleUseCase,
) : ViewModel() {

    // note: Common
    private val _signUpProcess = MutableStateFlow(0)
    val signUpProcess = _signUpProcess.asStateFlow()

    private val _currentPage = MutableStateFlow("agreement")
    val currentPage = _currentPage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    fun setSignUpProcess(process: Int) {
        _signUpProcess.value = process.coerceIn(0, 80)
    }

    fun setCurrentPage(page: String) {
        _currentPage.value = page
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun clear() {
        _signUpProcess.value = 0
        _currentPage.value = "agreement"
        _isLoading.value = false

        _socialToken.value = null
        _isAdult.value = false
        _isAgreeAll.value = false
        _isServiceAgreed.value = false
        _isPrivacyAgreed.value = false
        _isSensitiveAgreed.value = false
        _isMarketingAgreed.value = false

        _nickname.value = ""

        _partnerCoupleCode.value = null

        _isAgreementProcessed.value = false
        _isNicknameProcessed.value = false
        _isCoupleConnected.value = false

        CreateCoupleObserver.onCleared()
    }


    // note: Agreement Fragment
    private val _socialToken = MutableStateFlow<SocialToken?>(null)
    val socialToken = _socialToken.asStateFlow()

    private val _isAdult = MutableStateFlow(false)
    val isAdult = _isAdult.asStateFlow()

    private val _isAgreeAll = MutableStateFlow(false)
    val isAgreeAll = _isAgreeAll.asStateFlow()

    private val _isServiceAgreed = MutableStateFlow(false)
    val isServiceAgreed = _isServiceAgreed.asStateFlow()

    private val _isPrivacyAgreed = MutableStateFlow(false)
    val isPrivacyAgreed = _isPrivacyAgreed.asStateFlow()

    private val _isSensitiveAgreed = MutableStateFlow(false)
    val isSensitiveAgreed = _isSensitiveAgreed.asStateFlow()

    private val _isMarketingAgreed = MutableStateFlow(false)
    val isMarketingAgreed = _isMarketingAgreed.asStateFlow()

    private val _isAgreementProcessed = MutableStateFlow(false)
    val isAgreementProcessed = _isAgreementProcessed.asStateFlow()

    fun getSocialToken() = viewModelScope.launch {
        when (val result = getCachedSocialTokenUseCase()) {
            is Resource.Success -> {
                _socialToken.value = result.data
            }
            is Resource.Error -> {
                _socialToken.value = null
            }
        }
    }

    fun certifyAdult() {
        _isAdult.value = true
    }

    fun setAgreeAll(agreeAll: Boolean) {
        _isAgreeAll.value = agreeAll
    }

    fun setServiceAgreed(isAgreed: Boolean) {
        _isServiceAgreed.value = isAgreed
    }

    fun setPrivacyAgreed(isAgreed: Boolean) {
        _isPrivacyAgreed.value = isAgreed
    }

    fun setSensitiveAgreed(isAgreed: Boolean) {
        _isSensitiveAgreed.value = isAgreed
    }

    fun setMarketingAgreed(isAgreed: Boolean) {
        _isMarketingAgreed.value = isAgreed
    }

    fun setAgreementProcessed(processed: Boolean) = viewModelScope.launch {
        _isAgreementProcessed.value = processed
    }


    // note: Nickname Fragment
    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _isNicknameProcessed = MutableStateFlow(false)
    val isNicknameProcessed = _isNicknameProcessed.asStateFlow()

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setNicknameProcessed(processed: Boolean) = viewModelScope.launch {
        _isNicknameProcessed.value = processed
    }

    fun signUp() = viewModelScope.launch(Dispatchers.IO) {
        val fcmToken = FirebaseCloudMessagingService.getFcmToken() ?: run {
            infoLog("fcmToken is null.")
            _errorMessage.emit("잠시 후 다시 시도해주세요.")
            return@launch
        }

        setLoading(true)
        when (val result = signUpUseCase(_isMarketingAgreed.value, _nickname.value, fcmToken)) {
            is Resource.Success -> {
                getCoupleCode()
                setNicknameProcessed(true)
            }
            is Resource.Error -> {
                setNicknameProcessed(false)
                infoLog("회원가입 실패:${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
        setLoading(false)
    }


    // note: Couple Code Fragment
    private val _coupleCode = MutableStateFlow<String?>(null)
    val coupleCode = _coupleCode.asStateFlow()

    suspend fun getCoupleCode() = withContext(Dispatchers.IO) {
        when (val result = getCoupleCodeUseCase()) {
            is Resource.Success -> {
                _coupleCode.value = result.data.inviteCode
            }
            is Resource.Error -> {
                _coupleCode.value = null
                infoLog("커플코드 로딩 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
    }


    // note: Couple Connect Bottom Sheet
    private val _partnerCoupleCode = MutableStateFlow<String?>(null)
    val partnerCoupleCode = _partnerCoupleCode.asStateFlow()

    private val _isCoupleConnected = MutableStateFlow(false)
    val isCoupleConnected = _isCoupleConnected.asStateFlow()

    fun registerService() = viewModelScope.launch {
        CreateCoupleObserver
            .getInstance()
            .setCoupleCreated(false)
        CreateCoupleObserver
            .getInstance()
            .isCoupleCreated
            .collectLatest {
                runCatching {
                    _isCoupleConnected.value = it
                }.onFailure {
                    infoLog("CreateCoupleObserver Collect Error: ${it.localizedMessage}\n${it.stackTrace.joinToString("\n")}")
                }
            }
    }

    fun setPartnerCoupleCode(code: String) {
        _partnerCoupleCode.value = code
    }

    fun matchCoupleCode() = viewModelScope.launch {
        val partnerCode = _partnerCoupleCode.value ?: return@launch
        setLoading(true)

        when (val result = matchCoupleUseCase(partnerCode)) {
            is Resource.Success -> {
                _isCoupleConnected.value = true
            }
            is Resource.Error -> {
                infoLog("커플매칭 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }

        setLoading(false)
    }
}