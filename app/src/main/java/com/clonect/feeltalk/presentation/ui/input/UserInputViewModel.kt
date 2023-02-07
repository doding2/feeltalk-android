package com.clonect.feeltalk.presentation.ui.input

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UserInputViewModel @Inject constructor(

): ViewModel() {

    /** Nickname **/

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname = _nickname.asStateFlow()

    private val _invalidNicknameWarning = MutableStateFlow<String?>(null)
    val invalidNicknameWarning = _invalidNicknameWarning.asStateFlow()

    private val nicknamePattern = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]*$")

    fun checkValidNickname(nickname: String?): Boolean {
        return nickname?.let {
            nicknamePattern.matcher(nickname).matches()
        } ?: false
    }

    fun setNickname(nickname: String?) {
        _nickname.value = nickname
    }

    fun setInvalidNicknameWarning(text: String?) {
        _invalidNicknameWarning.value = text
    }


    /** Birth **/

    private val _birth = MutableStateFlow<String?>(null)
    val birth = _birth.asStateFlow()

    private val _invalidBirthWarning = MutableStateFlow<String?>(null)
    val invalidBirthWarning = _invalidBirthWarning.asStateFlow()

    fun setBirth(birth: String?) {
        _birth.value = birth
    }

    fun setInvalidBirthWarning(text: String?) {
        _invalidBirthWarning.value = text
    }

    fun checkValidDate(date: String): Boolean {
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        format.isLenient = false

        return try {
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Couple Anniversary **/

    private val _anniversary = MutableStateFlow<String?>(null)
    val anniversary = _anniversary.asStateFlow()

    private val _invalidAnniversaryWarning = MutableStateFlow<String?>(null)
    val invalidAnniversaryWarning = _invalidAnniversaryWarning.asStateFlow()

    fun setAnniversary(anniversary: String?) {
        _anniversary.value = anniversary
    }

    fun setInvalidAnniversaryWarning(text: String?) {
        _invalidAnniversaryWarning.value = text
    }


}