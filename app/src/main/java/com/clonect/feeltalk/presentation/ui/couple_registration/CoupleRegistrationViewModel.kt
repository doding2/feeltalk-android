package com.clonect.feeltalk.presentation.ui.couple_registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.GetCoupleRegistrationCodeUseCase
import com.clonect.feeltalk.domain.usecase.user.RemoveCoupleRegistrationCodeUseCase
import com.clonect.feeltalk.domain.usecase.user.SendPartnerCoupleRegistrationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CoupleRegistrationViewModel @Inject constructor(
    private val getCoupleRegistrationCodeUseCase: GetCoupleRegistrationCodeUseCase,
    private val sendPartnerCoupleRegistrationCodeUseCase: SendPartnerCoupleRegistrationCodeUseCase,
    private val removeCoupleRegistrationCodeUseCase: RemoveCoupleRegistrationCodeUseCase
) : ViewModel() {

    private val _myCoupleCode = MutableStateFlow("")
    val myCoupleCode = _myCoupleCode.asStateFlow()

    private val _partnerCoupleCodeInput = MutableStateFlow("")
    val partnerCoupleCodeInput = _partnerCoupleCodeInput.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        getCoupleRegistrationCode()
    }


    private fun getCoupleRegistrationCode() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleRegistrationCodeUseCase()
        when (result) {
            is Resource.Success -> _myCoupleCode.value = result.data
            is Resource.Error -> Log.i("CoupleRegFragment", "Fail to get couple registration code: ${result.throwable.localizedMessage}")
            else -> { }
        }
    }


    suspend fun sendPartnerCode() = withContext(Dispatchers.IO) {
        val partnerCode = _partnerCoupleCodeInput.value.trim()
        if (_myCoupleCode.value == partnerCode) return@withContext false

        val result = sendPartnerCoupleRegistrationCodeUseCase(partnerCode)
        return@withContext when (result) {
            is Resource.Success -> {
                val isValid = result.data.isValidCoupleCode
                if (isValid) {
                    removeCoupleRegistrationCodeUseCase()
                }
                isValid
            }
            is Resource.Error -> {
                _toastMessage.emit(result.throwable.localizedMessage ?: "Unexpected error is occurred.")
                false
            }
            else -> {
                false
            }
        }
    }


    fun setPartnerCodeInput(input: String) {
        _partnerCoupleCodeInput.value = input
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toastMessage.emit(message)
    }
}