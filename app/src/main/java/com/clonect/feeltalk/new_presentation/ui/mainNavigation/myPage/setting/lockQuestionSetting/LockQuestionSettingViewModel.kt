package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockQuestionSetting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LockQuestionSettingViewModel @Inject constructor(

): ViewModel() {

    private val _password = MutableStateFlow<String?>(null)
    val password = _password.asStateFlow()

    fun setPassword(password: String?) {
        _password.value = password
    }
}