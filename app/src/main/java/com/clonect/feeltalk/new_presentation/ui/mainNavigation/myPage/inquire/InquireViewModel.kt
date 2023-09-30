package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.inquire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.usecase.account.SubmitSuggestionUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/24.
 */
@HiltViewModel
class InquireViewModel @Inject constructor(
    private val submitSuggestionUseCase: SubmitSuggestionUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _isSubmitEnabled = MutableStateFlow(false)
    val isSubmitEnabled = _isSubmitEnabled.asStateFlow()


    private val _focusedEditText = MutableStateFlow<String?>(null)
    val focusedEditText = _focusedEditText.asStateFlow()


    private val _title = MutableStateFlow<String?>(null)
    val title = _title.asStateFlow()

    private val _body = MutableStateFlow<String?>(null)
    val body = _body.asStateFlow()

    private val _email = MutableStateFlow<String?>(null)
    val email = _email.asStateFlow()


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


    fun setTitle(title: String?) = viewModelScope.launch {
        _title.value = title
        computeSubmitEnabled()
    }

    fun setBody(body: String?) = viewModelScope.launch {
        _body.value = body
        computeSubmitEnabled()
    }

    fun setEmail(email: String?) = viewModelScope.launch {
        _email.value = email
        computeSubmitEnabled()
    }

    private fun computeSubmitEnabled() {
        _isSubmitEnabled.value = !title.value.isNullOrBlank()
                && !body.value.isNullOrBlank()
                && !email.value.isNullOrBlank()
    }


    fun isEdited(): Boolean {
        return !title.value.isNullOrBlank()
                || !body.value.isNullOrBlank()
                || !email.value.isNullOrBlank()
    }


    fun submitInquiry(onComplete: () -> Unit) = viewModelScope.launch {
        val title = title.value ?: return@launch
        val body = body.value ?: return@launch
        val email = email.value ?: return@launch
        setLoading(true)
        when (val result = submitSuggestionUseCase(title, body, email)) {
            is Resource.Success -> {
                onComplete()
            }
            is Resource.Error -> {
                infoLog("Fail to submit inquiry: ${result.throwable.localizedMessage}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
        setLoading(false)
    }
}