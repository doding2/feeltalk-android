package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockSetting.lockQuestionSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.usecase.account.LockAccountUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LockQuestionSettingViewModel @Inject constructor(
    private val lockAccountUseCase: LockAccountUseCase,
): ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _isLockAnswerFocused = MutableStateFlow(false)
    val isLockAnswerFocused = _isLockAnswerFocused.asStateFlow()

    private val _containSpecialChar = MutableStateFlow(false)
    val containSpecialChar = _containSpecialChar.asStateFlow()

    private val _isAddEnabled = MutableStateFlow(false)
    val isAddEnabled = _isAddEnabled.asStateFlow()


    private val _password = MutableStateFlow<String?>(null)
    val password = _password.asStateFlow()

    private val _questionType = MutableStateFlow<Int?>(null)
    val questionType = _questionType.asStateFlow()

    private val _lockAnswer = MutableStateFlow<String?>(null)
    val lockAnswer = _lockAnswer.asStateFlow()

    private val _lockAnswerDate = MutableStateFlow(Date())
    val lockAnswerDate = _lockAnswerDate.asStateFlow()



    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }

    fun setLockAnswerFocused(focused: Boolean) {
        _isLockAnswerFocused.value = focused
    }

    private fun checkContainSpecialChar() {
        val answer = _lockAnswer.value
        val pattern = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]*$")
        val isNoSpecialCharacter = pattern.matcher(answer).matches()
        _containSpecialChar.value = !isNoSpecialCharacter
    }


    fun setPassword(password: String?) {
        _password.value = password
    }

    fun setQuestionType(questionType: Int) {
        _questionType.value = questionType
        computeAddButtonEnabled()
    }

    fun setLockAnswer(answer: String?) {
        _lockAnswer.value = answer
        checkContainSpecialChar()
        computeAddButtonEnabled()
    }

    fun setLockAnswerDate(date: Date) {
        _lockAnswerDate.value = date
    }


    private fun computeAddButtonEnabled() {
        val isAddEnabled = when (_questionType.value) {
            0, 1, 3, 4 -> lockAnswer.value.isNullOrBlank().not() && !containSpecialChar.value
            2 -> true
            null -> false
            else -> false
        }
        _isAddEnabled.value = isAddEnabled
    }


    fun lockAccount(onComplete: () -> Unit) = viewModelScope.launch {
        val password = _password.value ?: return@launch
        val questionType = _questionType.value ?: return@launch
        val answer = when (questionType) {
            2 -> {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = _lockAnswerDate.value
                format.format(date)
            }
            else -> _lockAnswer.value ?: return@launch
        }
        val qa = LockQA(questionType, answer)

        setLoading(true)
        when (val result = lockAccountUseCase(password, qa)) {
            is Resource.Success -> {
                onComplete()
            }
            is Resource.Error -> {
                infoLog("Fail to lock account: ${result.throwable.localizedMessage}")
                sendErrorMessage(result.throwable.localizedMessage ?: "Fail to lock account")
            }
        }
        setLoading(false)
    }
}