package com.clonect.feeltalk.presentation.ui.couple_registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.GetCoupleRegistrationCodeUseCase
import com.clonect.feeltalk.domain.usecase.user.RemoveCoupleRegistrationCodeUseCase
import com.clonect.feeltalk.domain.usecase.user.SendPartnerCoupleRegistrationCodeUseCase
import com.clonect.feeltalk.presentation.service.notification_observer.CoupleRegistrationObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoupleRegistrationViewModel @Inject constructor(
    private val getCoupleRegistrationCodeUseCase: GetCoupleRegistrationCodeUseCase,
    private val sendPartnerCoupleRegistrationCodeUseCase: SendPartnerCoupleRegistrationCodeUseCase,
    private val removeCoupleRegistrationCodeUseCase: RemoveCoupleRegistrationCodeUseCase
) : ViewModel() {

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _myCoupleCode = MutableStateFlow("")
    val myCoupleCode = _myCoupleCode.asStateFlow()

    private val _partnerCoupleCodeInput = MutableStateFlow("")
    val partnerCoupleCodeInput = _partnerCoupleCodeInput.asStateFlow()

    private val _isCoupleRegistrationCompleted = MutableStateFlow(false)
    val isCoupleRegistrationCompleted = _isCoupleRegistrationCompleted.asStateFlow()


    init {
        getCoupleRegistrationCode()
        collectIsCoupleRegistrationCompleted()
    }


    private fun collectIsCoupleRegistrationCompleted() = viewModelScope.launch(Dispatchers.IO) {
        CoupleRegistrationObserver.getInstance()
            .isCoupleRegistrationCompleted.collectLatest { isCompleted ->
                _isCoupleRegistrationCompleted.value = isCompleted
            }
    }

    private fun getCoupleRegistrationCode() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleRegistrationCodeUseCase()
        when (result) {
            is Resource.Success -> _myCoupleCode.value = result.data
            is Resource.Error -> {
                Log.i("CoupleRegFragment",
                    "Fail to get couple registration code: ${result.throwable.localizedMessage}")

                sendToast("내 초대코드를 불러오는데 실패했습니다.")
            } else -> { }
        }
    }


    suspend fun sendPartnerCode() = viewModelScope.launch(Dispatchers.IO) {
        val partnerCode = _partnerCoupleCodeInput.value.trim()
        if (_myCoupleCode.value == partnerCode) return@launch

        val result = sendPartnerCoupleRegistrationCodeUseCase(partnerCode)
        when (result) {
            is Resource.Success -> {
                val isValid = result.data.isValidCoupleCode
                if (isValid) {
                    removeCoupleRegistrationCodeUseCase()
                } else {
                    sendToast("올바르지 않은 초대코드입니다.")
                }
                _isCoupleRegistrationCompleted.value = isValid
            }
            is Resource.Error -> {
                sendToast("초대코드 확인에 에러가 발생했습니다.")
                Log.i("CoupleRegFragment", "send partner code error: ${result.throwable.localizedMessage ?: "Unexpected error is occurred."}")
                _isCoupleRegistrationCompleted.value = false
            }
            else -> {
                _isCoupleRegistrationCompleted.value = false
            }
        }
    }

    fun setPartnerCodeInput(input: String) {
        _partnerCoupleCodeInput.value = input
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toastMessage.emit(message)
    }

    override fun onCleared() {
        super.onCleared()
        CoupleRegistrationObserver.onCleared()
    }
}