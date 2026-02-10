package com.clonect.feeltalk.release_presentation.ui.passwordNavigation.lockReset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.usecase.account.GetLockQAUseCase
import com.clonect.feeltalk.release_domain.usecase.account.ValidateLockResetAnswerUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/19.
 */
@HiltViewModel
class LockResetViewModel @Inject constructor(
    private val getLockQAUseCase: GetLockQAUseCase,
    private val validateLockResetAnswerUseCase: ValidateLockResetAnswerUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _isLockAnswerFocused = MutableStateFlow(false)
    val isLockAnswerFocused = _isLockAnswerFocused.asStateFlow()

    private val _isConfirmEnabled = MutableStateFlow(false)
    val isConfirmEnabled = _isConfirmEnabled.asStateFlow()

    private val _showInvalidWarning = MutableStateFlow(false)
    val showInvalidWarning = _showInvalidWarning.asStateFlow()


    private val _lockQuestionType = MutableStateFlow<Int?>(null)
    val lockQuestionType = _lockQuestionType.asStateFlow()

    private val _lockAnswer = MutableStateFlow<String?>(null)
    val lockAnswer = _lockAnswer.asStateFlow()

    private val _lockAnswerDate = MutableStateFlow(Date())
    val lockAnswerDate = _lockAnswerDate.asStateFlow()


    init {
        initLockQA()
        computeConfirmButtonEnabled()
    }


    private fun initLockQA() = viewModelScope.launch {
        setLoading(true)
        when (val result = getLockQAUseCase()) {
            is Resource.Success -> {
                _lockQuestionType.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get lock QA: ${result.throwable.localizedMessage}")
            }
        }
        setLoading(false)
    }



    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }

    fun setKeyboardUp(isUp: Boolean) {
        if (isUp) {
            setShowInvalidAnswer(false)
        }
        _isKeyboardUp.value = isUp
    }

    fun setLockAnswerFocused(focused: Boolean) {
        _isLockAnswerFocused.value = focused
    }

    fun setShowInvalidAnswer(isInvalid: Boolean) {
        _showInvalidWarning.value = isInvalid
    }


    fun setLockAnswer(answer: String?) {
        setShowInvalidAnswer(false)
        _lockAnswer.value = answer
        computeConfirmButtonEnabled()
    }

    fun setLockAnswerDate(date: Date) {
        setShowInvalidAnswer(false)
        _lockAnswerDate.value = date
        computeConfirmButtonEnabled()
    }

    private fun computeConfirmButtonEnabled() {
        val isAddEnabled = when (lockQuestionType.value) {
            0, 1, 3, 4 -> _lockAnswer.value.isNullOrBlank().not()
            2 -> true
            null -> false
            else -> false
        }
        _isConfirmEnabled.value = isAddEnabled
    }


    suspend fun matchQuestionAnswer(): Boolean = withContext(viewModelScope.coroutineContext) {
        val questionType = lockQuestionType.value ?: return@withContext false
        val answer = when (questionType) {
            0, 1, 3, 4 -> lockAnswer.value ?: return@withContext false
            2 -> {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                format.format(lockAnswerDate.value)
            }
            else -> return@withContext false
        }
        setLoading(true)
        val isValid = when (val result = validateLockResetAnswerUseCase(answer)) {
            is Resource.Success -> {
                result.data.isValid
            }
            is Resource.Error -> {
                infoLog("Fail to match question answer: ${result.throwable.localizedMessage}")
                false
            }
        }
        setLoading(false)
        setShowInvalidAnswer(!isValid)
        return@withContext isValid
    }

}