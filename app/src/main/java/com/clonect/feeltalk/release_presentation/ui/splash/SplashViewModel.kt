package com.clonect.feeltalk.release_presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.FeelTalkException
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.release_domain.usecase.account.AutoLogInUseCase
import com.clonect.feeltalk.release_domain.usecase.account.CheckAccountLockedUseCase
import com.clonect.feeltalk.release_domain.usecase.newAccount.GetUserStatusNewUseCase
import com.clonect.feeltalk.release_domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.release_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.release_domain.usecase.signal.GetPartnerSignalUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Created by doding2 on 2024/01/08.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val autoLogInUseCase: AutoLogInUseCase,
    private val checkAccountLockedUseCase: CheckAccountLockedUseCase,
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val getMySignalUseCase: GetMySignalUseCase,
    private val getPartnerSignalUseCase: GetPartnerSignalUseCase,

    private val getUserStatusNewUseCase: GetUserStatusNewUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }


    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isUser = MutableStateFlow(false)
    val isUser = _isUser.asStateFlow()

    private val _isUserCouple = MutableStateFlow(false)
    val isUserCouple = _isUserCouple.asStateFlow()


    private val _isAccountLocked = MutableStateFlow(false)
    val isAccountLocked = _isAccountLocked.asStateFlow()


    private val _isServerDown = MutableStateFlow(false)
    val isServerDown = _isServerDown.asStateFlow()

    private val _isNetworkErrorOccurred = MutableStateFlow(false)
    val isNetworkErrorOccurred = _isNetworkErrorOccurred.asStateFlow()


    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()


    suspend fun getUserStatus() = withContext(Dispatchers.IO) {
        getUserStatusNewUseCase()
            .onSuccess {
                when (it.memberStatus.lowercase()) {
                    "newbie" -> {
                        _isUser.value = false
                    }
                    "solo" -> {
                        _isUser.value = true
                        _isUserCouple.value = false
                    }
                    "couple" -> {
                        preloadCoupleData()
                        _isUser.value = true
                        _isUserCouple.value = true
                    }
                }
                _isLoggedIn.value = true
                infoLog("Success to get user status")
            }
            .onError {
                if (it is FeelTalkException.ServerIsDownException) {
                    _isServerDown.value = true
                }
                if (it is UnknownHostException) {
                    _isNetworkErrorOccurred.value = true
                }
                _isLoggedIn.value = false
                infoLog("Fail to get user status: ${it.localizedMessage}")
                setReady()
            }
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
                        preloadCoupleData()
                        _isUser.value = true
                        _isUserCouple.value = true
                    }
                }
                _isLoggedIn.value = true
                infoLog("Success to auto log in")
            }
            is Resource.Error -> {
                if (result.throwable is FeelTalkException.ServerIsDownException) {
                    _isServerDown.value = true
                }
                if (result.throwable is UnknownHostException) {
                    _isNetworkErrorOccurred.value = true
                }
                _isLoggedIn.value = false
                result.throwable.printStackTrace()
                infoLog("Fail to auto log in: ${result.throwable}\n${result.throwable.stackTrace.joinToString("\n")}")
                setReady()
            }
        }
    }


    private suspend fun preloadCoupleData() = withContext(Dispatchers.IO) {
        val isAccountLocked = async {
            when (val result = checkAccountLockedUseCase()) {
                is Resource.Success -> {
                    _isAccountLocked.value = result.data
                    infoLog("isAccountLocked: ${result.data}")
                }
                is Resource.Error -> {
                    result.throwable.printStackTrace()
                    infoLog("Fail to check account locked: ${result.throwable}\n${result.throwable.stackTrace.joinToString("\n")}")
                }
            }
        }

        val todayQuestion = async {
            when (val result = getTodayQuestionUseCase()) {
                is Resource.Success -> { }
                is Resource.Error -> {
                    if (result.throwable is FeelTalkException.ServerIsDownException) {
                        _isServerDown.value = true
                    }
                    if (result.throwable is UnknownHostException) {
                        _isNetworkErrorOccurred.value = true
                    }
                    infoLog("Fail to preload today question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
                }
            }
        }

        val mySignal = async {
            getMySignalUseCase()
                .onError {
                    if (it is FeelTalkException.ServerIsDownException) {
                        _isServerDown.value = true
                    }
                    if (it is UnknownHostException) {
                        _isNetworkErrorOccurred.value = true
                    }
                    infoLog("Fail to get my signal: ${it.localizedMessage}")
                }
        }

        val partnerSignal = async {
            getPartnerSignalUseCase()
                .onError {
                    if (it is FeelTalkException.ServerIsDownException) {
                        _isServerDown.value = true
                    }
                    if (it is UnknownHostException) {
                        _isNetworkErrorOccurred.value = true
                    }
                    infoLog("Fail to get partner signal: ${it.localizedMessage}")
                }
        }

        todayQuestion.await()
        isAccountLocked.await()
        mySignal.await()
        partnerSignal.await()
    }



    fun setReady() {
        _isReady.value = true
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toast.emit(message)
    }

}