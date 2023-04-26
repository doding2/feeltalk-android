package com.clonect.feeltalk.presentation.ui.key_restoring_accept

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.domain.usecase.encryption.CheckKeyPairsExistUseCase
import com.clonect.feeltalk.domain.usecase.encryption.CheckKeyPairsWorkWellUseCase
import com.clonect.feeltalk.domain.usecase.encryption.HelpToRestoreKeysUseCase
import com.clonect.feeltalk.presentation.ui.key_restoring_request.KeyPairsState
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyRestoringAcceptViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val checkKeyPairsExistUseCase: CheckKeyPairsExistUseCase,
    private val checkKeyPairsWorkWellUseCase: CheckKeyPairsWorkWellUseCase,
    private val helpToRestoreKeysUseCase: HelpToRestoreKeysUseCase,
): ViewModel() {

    private val _partnerState = MutableStateFlow(context.getString(R.string.key_restoring_accept_request))
    val partnerState = _partnerState.asStateFlow()

    private val _keyPairsStateMessage = MutableStateFlow(
        KeyPairsState(
            message = context.getString(R.string.key_restoring_state_message_loading),
            state = Emotion.Puzzling
        )
    )
    val keyPairsStateMessage = _keyPairsStateMessage.asStateFlow()

    private val _acceptButtonEnabled = MutableStateFlow(false)
    val acceptButtonEnabled = _acceptButtonEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()

    init {
        checkKeyPairsExist()
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
                    enableAcceptButton(false)
                } else {
                    checkKeyPairsWorkWell()
                }
            }
            is Resource.Error -> {
                infoLog("Fail to check key pairs exist: ${result.throwable.localizedMessage}")
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_missing),
                    state = Emotion.Puzzling
                )
                enableAcceptButton(false)
            }
            else -> {
                infoLog("Fail to check key pairs exist")
                enableAcceptButton(false)
            }
        }
    }

    fun checkKeyPairsWorkWell() = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        val result = checkKeyPairsWorkWellUseCase()
        setLoading(false)
        when (result) {
            is Resource.Success -> {
                val workWell = result.data
                _keyPairsStateMessage.value = KeyPairsState(
                    message = if (workWell) context.getString(R.string.key_restoring_state_message_normal) else context.getString(
                        R.string.key_restoring_state_message_corrupted),
                    state = if (workWell) Emotion.Happy else Emotion.Angry
                )
                enableAcceptButton(workWell)
            }
            is Resource.Error -> {
                infoLog("Fail to check key pairs work well: ${result.throwable.localizedMessage}")
                _keyPairsStateMessage.value = KeyPairsState(
                    message = context.getString(R.string.key_restoring_state_message_corrupted),
                    state = Emotion.Bad
                )
                enableAcceptButton(false)
            }
            else -> {
                infoLog("Fail to check key pairs work well")
                enableAcceptButton(false)
            }
        }
    }


    fun acceptRestoreKeys() = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        val result = helpToRestoreKeysUseCase()
        setLoading(false)
        when (result) {
            is Resource.Success -> {
                toast("연인의 암호화 열쇠 복구에 성공했습니다")
                _partnerState.value = context.getString(R.string.key_restoring_accept_success)
                enableAcceptButton(false)
            }
            is Resource.Error -> {
                infoLog("Fail to help to restore keys: ${result.throwable.localizedMessage}")
                toast("연인의 암호화 열쇠 복구에 실패했습니다")
                enableAcceptButton(true)

            }
            else -> {
                infoLog("Fail to help to restore keys")
                toast("연인의 암호화 열쇠 복구에 실패했습니다")
                enableAcceptButton(true)
            }
        }
    }


    fun enableAcceptButton(enabled: Boolean) {
        _acceptButtonEnabled.value = enabled
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun toast(message: String) = viewModelScope.launch(Dispatchers.IO) {
        _toast.emit(message)
    }
}