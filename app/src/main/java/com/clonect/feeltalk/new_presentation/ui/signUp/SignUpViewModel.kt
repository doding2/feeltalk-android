package com.clonect.feeltalk.new_presentation.ui.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.usecase.signIn.CheckMemberTypeUseCase
import com.clonect.feeltalk.new_domain.usecase.token.CacheSocialTokenUseCase
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
    private val checkMemberTypeUseCase: CheckMemberTypeUseCase,
    private val cacheSocialTokenUseCase: CacheSocialTokenUseCase,
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


    suspend fun checkMemberType(socialToken: SocialToken) = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        when (val result = checkMemberTypeUseCase(socialToken)) {
            is Resource.Success -> {
                setLoading(false)
                when (result.data.type) {
                    "newbie" -> {
                        cacheSocialToken(socialToken)
                        _navigateToAgreement.emit(true)
                    }
                    "solo" -> {
                        // TODO 자동 로그인 하기
//                        _navigateToCoupleCode.emit(true)
                    }
                    "couple" -> {
                        // TODO 자동 로그인 하기
//                        _navigateToMain.emit(true)
                    }
                }
            }
            is Resource.Error -> {
                setLoading(false)
                _errorMessage.emit(result.throwable.localizedMessage ?: "네트워크 연결을 확인해주세요.")
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