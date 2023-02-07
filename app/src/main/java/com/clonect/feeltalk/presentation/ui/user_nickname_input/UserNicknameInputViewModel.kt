package com.clonect.feeltalk.presentation.ui.user_nickname_input

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UserNicknameInputViewModel @Inject constructor(
    stateHandle: SavedStateHandle
): ViewModel() {

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname = _nickname.asStateFlow()

    private val _invalidWarning = MutableStateFlow<String?>(null)
    val invalidWarning = _invalidWarning.asStateFlow()


    private val pattern = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]*$")

    init {
        _nickname.value = stateHandle["nickname"]
    }

    fun checkValidNickname(nickname: String?): Boolean {
        return nickname?.let {
            pattern.matcher(nickname).matches()
        } ?: false
    }


    fun setNickname(nickname: String?) {
        _nickname.value = nickname
    }

    fun setInvalidWarning(text: String?) {
        _invalidWarning.value = text
    }

}