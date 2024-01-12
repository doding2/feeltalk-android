package com.clonect.feeltalk.new_presentation.ui.passwordNavigation.password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.usecase.account.MatchPasswordUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/19.
 */
@HiltViewModel
class PasswordViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val matchPasswordUseCase: MatchPasswordUseCase,
) : ViewModel() {

    private val defaultErrorMessage = context.getString(R.string.pillowtalk_default_error_message)

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isValidPassword = MutableStateFlow(true)
    val isValidPassword = _isValidPassword.asStateFlow()

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setValidPassword(isValid: Boolean) {
        _isValidPassword.value = isValid
    }

    fun addPasswordNum(num: Int) = viewModelScope.launch {
        setValidPassword(true)
        val password = _password.value
        if (password.length < 4) {
            _password.value = password + num
        }
    }

    fun clearPassword() = viewModelScope.launch {
        _password.value = ""
    }


    suspend fun matchPassword(): Boolean = withContext(Dispatchers.IO) {
        val password = _password.value
        if (password.length < 4) return@withContext false

//        setLoading(true)
        val result = matchPasswordUseCase(password)
//        setLoading(false)
        return@withContext  when (result) {
            is Resource.Success -> {
                val isValid = result.data
                if (!isValid) {
                    setValidPassword(false)
                    clearPassword()
                }
                isValid
            }
            is Resource.Error -> {
                infoLog("Fail to match password: ${result.throwable.localizedMessage}")
                sendErrorMessage(defaultErrorMessage)
                false
            }
        }
    }

}