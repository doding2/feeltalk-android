package com.clonect.feeltalk.presentation.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.UpdateUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UserInputViewModel @Inject constructor(
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
): ViewModel() {

    /** Nickname **/

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname = _nickname.asStateFlow()

    private val _invalidNicknameWarning = MutableStateFlow<String?>(null)
    val invalidNicknameWarning = _invalidNicknameWarning.asStateFlow()

    private val nicknamePattern = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9\\s]*$")

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

    private val _coupleAnniversary = MutableStateFlow<String?>(null)
    val coupleAnniversary = _coupleAnniversary.asStateFlow()

    private val _invalidCoupleAnniversaryWarning = MutableStateFlow<String?>(null)
    val invalidCoupleAnniversaryWarning = _invalidCoupleAnniversaryWarning.asStateFlow()

    private val _isUserInfoUpdateCompleted = MutableStateFlow(false)
    val isUserInfoUpdateCompleted = _isUserInfoUpdateCompleted.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    fun setCoupleAnniversary(anniversary: String?) {
        _coupleAnniversary.value = anniversary
    }

    fun setInvalidCoupleAnniversaryWarning(text: String?) {
        _invalidCoupleAnniversaryWarning.value = text
    }

    fun updateUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.value = true
        val result = updateUserInfoUseCase(
            nickname = _nickname.value ?: "",
            birthDate = _birth.value ?: "0001/01/01",
            anniversary = _coupleAnniversary.value ?: "0001/01/01"
        )
        val isSuccessful = when (result) {
            is Resource.Success -> result.data.status == "ok"
            else -> false
        }
        _isLoading.value = false
        _isUserInfoUpdateCompleted.value = isSuccessful
    }

    fun clear() {
        _nickname.value = null
        _invalidNicknameWarning.value = null
        _birth.value = null
        _invalidBirthWarning.value = null
        _coupleAnniversary.value = null
        _invalidCoupleAnniversaryWarning.value = null
    }

}