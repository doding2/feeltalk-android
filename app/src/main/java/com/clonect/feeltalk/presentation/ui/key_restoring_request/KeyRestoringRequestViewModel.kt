package com.clonect.feeltalk.presentation.ui.key_restoring_request

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.domain.usecase.encryption.CheckKeyPairsExistUseCase
import com.clonect.feeltalk.domain.usecase.encryption.CheckKeyPairsWorkWellUseCase
import com.clonect.feeltalk.domain.usecase.encryption.RequestToRestoreKeysUseCase
import com.clonect.feeltalk.domain.usecase.encryption.RestoreKeysUseCase
import com.clonect.feeltalk.new_presentation.service.notification.observer.AcceptRestoringKeysRequestObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyRestoringRequestViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val checkKeyPairsExistUseCase: CheckKeyPairsExistUseCase,
    private val checkKeyPairsWorkWellUseCase: CheckKeyPairsWorkWellUseCase,
    private val requestToRestoreKeysUseCase: RequestToRestoreKeysUseCase,
    private val restoreKeysUseCase: RestoreKeysUseCase,
): ViewModel() {

    private val _keyPairsStateMessage = MutableStateFlow(
        KeyPairsState(
            message = context.getString(R.string.key_restoring_state_message_loading),
            state = Emotion.Puzzling
        )
    )
    val keyPairsStateMessage = _keyPairsStateMessage.asStateFlow()

    private val _requestButtonEnabled = MutableStateFlow(false)
    val requestButtonEnabled = _requestButtonEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()


    init {
        checkKeyPairsExist()
        collectRequestOk()
    }

    private fun collectRequestOk() = viewModelScope.launch(Dispatchers.IO) {
        AcceptRestoringKeysRequestObserver
            .getInstance()
            .isPartnerAccepted
            .collectLatest { isOk ->
                if (isOk) {
                    restoreKeys()
                }
            }
    }

    fun checkKeyPairsExist() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = checkKeyPairsExistUseCase()) {
            is Resource.Success -> {
                val isExist = result.data
                if (!isExist) {
                    _keyPairsStateMessage.value = KeyPairsState(
                        message = context.getString(R.string.key_restoring_state_message_missing),
                        state = Emotion.Puzzling
                    )
                    enableRequestButton(true)
                    infoLog("Key Pair doesn't exist")
                }
                else {
                    infoLog("Key Pair Exist")
                    checkKeyPairsWorkWell()
                }
            }
            is Resource.Error -> {
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_missing),
                    state = Emotion.Puzzling
                )
                infoLog("Fail to check key pairs exist: ${result.throwable.localizedMessage}")
                enableRequestButton(true)
            }
            else -> {
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_missing),
                    state = Emotion.Puzzling
                )
                infoLog("Fail to check key pairs exist")
                enableRequestButton(true)
            }
        }
    }

    fun checkKeyPairsWorkWell() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = checkKeyPairsWorkWellUseCase()) {
            is Resource.Success -> {
                val workWell = result.data
                _keyPairsStateMessage.value = KeyPairsState(
                    message = if (workWell) context.getString(R.string.key_restoring_state_message_normal) else context.getString(R.string.key_restoring_state_message_corrupted),
                    state = if (workWell) Emotion.Happy else Emotion.Angry
                )
                enableRequestButton(workWell.not())
                infoLog("No Problem At Key Pairs")
            }
            is Resource.Error -> {
                infoLog("Fail to check key pairs work well: ${result.throwable.localizedMessage}")
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_corrupted),
                    state = Emotion.Bad
                )
                enableRequestButton(true)
            }
            else -> {
                infoLog("Fail to check key pairs work well")
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_corrupted),
                    state = Emotion.Bad
                )
                enableRequestButton(true)
            }
        }
    }


    fun requestToRestoreKeys() = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        val result = requestToRestoreKeysUseCase()
        setLoading(false)
        when (result) {
            is Resource.Success -> {
                toast("연인에게 복구 요청을 보냈습니다")
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_waiting),
                    state = Emotion.Puzzling
                )
                enableRequestButton(false)
            }
            is Resource.Error -> {
                infoLog("Fail to send request to restore keys: ${result.throwable.localizedMessage}")
                toast("복구 요청에 실패했습니다")
                enableRequestButton(true)
            }
            else -> {
                infoLog("Fail to send request to restore keys")
                toast("복구 요청에 실패했습니다")
                enableRequestButton(true)
            }
        }
    }


    private fun restoreKeys() = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        val result = restoreKeysUseCase()
        setLoading(false)
        when (result) {
            is Resource.Success -> {
                toast("암호화 열쇠 복구에 성공했습니다.")
            } 
            is Resource.Error -> {
                toast("암호화 열쇠 복구에 실패했습니다.")
                infoLog("Fail to restore key pairs: ${result.throwable.localizedMessage}")
                AcceptRestoringKeysRequestObserver.getInstance().setPartnerAccepted(false)
            }
            else -> {
                toast("암호화 열쇠 복구에 실패했습니다.")
                infoLog("Fail to restore key pairs")
                AcceptRestoringKeysRequestObserver.getInstance().setPartnerAccepted(false)
            }
        }
        checkKeyPairsExist()
    }


    fun enableRequestButton(enabled: Boolean) {
        _requestButtonEnabled.value = enabled
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun toast(message: String) = viewModelScope.launch(Dispatchers.IO) {
        _toast.emit(message)
    }


    override fun onCleared() {
        super.onCleared()
        AcceptRestoringKeysRequestObserver.onCleared()
    }
}