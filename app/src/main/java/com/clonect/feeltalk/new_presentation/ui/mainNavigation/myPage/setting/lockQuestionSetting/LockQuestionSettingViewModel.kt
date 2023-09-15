package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockQuestionSetting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LockQuestionSettingViewModel @Inject constructor(

): ViewModel() {

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _isLockAnswerFocused = MutableStateFlow(false)
    val isLockAnswerFocused = _isLockAnswerFocused.asStateFlow()

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


    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }

    fun setLockAnswerFocused(focused: Boolean) {
        _isLockAnswerFocused.value = focused
    }


    fun setPassword(password: String?) {
        _password.value = password
    }

    fun setQuestionType(questionType: Int) {
        if (questionType < 0 || questionType > 2) return
        _questionType.value = questionType
        computeAddButtonEnabled()
    }

    fun setLockAnswer(answer: String?) {
        _lockAnswer.value = answer
        computeAddButtonEnabled()
    }

    fun setLockAnswerDate(date: Date) {
        _lockAnswerDate.value = date
    }


    private fun computeAddButtonEnabled() {
        val isAddEnabled = when (_questionType.value) {
            0, 2 -> _lockAnswer.value.isNullOrBlank().not()
            1 -> true
            null -> false
            else -> false
        }
        _isAddEnabled.value = isAddEnabled
    }
}