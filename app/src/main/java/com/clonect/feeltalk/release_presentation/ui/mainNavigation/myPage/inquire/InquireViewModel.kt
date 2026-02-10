package com.clonect.feeltalk.release_presentation.ui.mainNavigation.myPage.inquire

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.usecase.account.SubmitSuggestionUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext context: Context,
    private val submitSuggestionUseCase: SubmitSuggestionUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
) : ViewModel() {

    private val defaultErrorMessage = context.getString(R.string.pillowtalk_default_error_message)

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
                && validateEmail(email.value)
    }


    fun isEdited(): Boolean {
        return !title.value.isNullOrBlank()
                || !body.value.isNullOrBlank()
                || !email.value.isNullOrBlank()
    }

    private fun validateEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
                sendErrorMessage(defaultErrorMessage)
            }
        }
        setLoading(false)
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }
}