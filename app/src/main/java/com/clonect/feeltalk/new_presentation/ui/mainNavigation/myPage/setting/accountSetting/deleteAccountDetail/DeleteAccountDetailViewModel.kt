package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.deleteAccountDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/23.
 */
@HiltViewModel
class DeleteAccountDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _focusedEditText = MutableStateFlow<String?>(null)
    val focusedEditText = _focusedEditText.asStateFlow()

    private val _isConfirmEnabled = MutableStateFlow(false)
    val isConfirmEnabled = _isConfirmEnabled.asStateFlow()


    private val _deleteReasonType = MutableStateFlow<DeleteReasonType?>(null)
    val deleteReasonType = _deleteReasonType.asStateFlow()

    private val _etcReason = MutableStateFlow("")
    val etcReason = _etcReason.asStateFlow()

    private val _deleteReason = MutableStateFlow("")
    val deleteReason = _deleteReason.asStateFlow()


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }

    fun setKeyboardUp(isUp: Boolean) = viewModelScope.launch {
        _isKeyboardUp.value = isUp
    }

    fun setFocusedEditText(et: String?) = viewModelScope.launch {
        _focusedEditText.value = et
    }

    private fun computeConfirmEnabled() = viewModelScope.launch {
        val type = _deleteReasonType.value
        _isConfirmEnabled.value = when (type) {
            null -> false
            DeleteReasonType.BreakUp,
            DeleteReasonType.NoFunctionality,
            DeleteReasonType.BugOrError -> _deleteReason.value.isNotBlank()
            DeleteReasonType.Etc -> _etcReason.value.isNotBlank() && _deleteReason.value.isNotBlank()
        }
    }


    fun setDeleteReasonType(type: DeleteReasonType) = viewModelScope.launch {
        _deleteReasonType.value = type
        computeConfirmEnabled()
    }

    fun setEtcReason(reason: String) = viewModelScope.launch {
        _etcReason.value = reason
        computeConfirmEnabled()
    }

    fun setDeleteReason(reason: String) = viewModelScope.launch {
        _deleteReason.value = reason
        computeConfirmEnabled()
    }


    fun deleteAccount(onComplete: () -> Unit) = viewModelScope.launch {
        setLoading(true)
        setLoading(false)
        onComplete()
    }

}