package com.clonect.feeltalk.presentation.ui.couple_registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.encryption.*
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.new_presentation.notification.observer.CreateCoupleObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CoupleRegistrationViewModel @Inject constructor(
    private val getCoupleRegistrationCodeUseCase: GetCoupleRegistrationCodeUseCase,
    private val sendPartnerCoupleRegistrationCodeUseCase: SendPartnerCoupleRegistrationCodeUseCase,
    private val removeCoupleRegistrationCodeUseCase: RemoveCoupleRegistrationCodeUseCase,
    private val uploadMyPublicKeyUseCase: UploadMyPublicKeyUseCase,
    private val loadPartnerPublicKeyUseCase: LoadPartnerPublicKeyUseCase,
    private val uploadMyPrivateKeyUseCase: UploadMyPrivateKeyUseCase,
    private val loadPartnerPrivateKeyUseCase: LoadPartnerPrivateKeyUseCase,
    private val checkKeyPairsExistUseCase: CheckKeyPairsExistUseCase,
    private val checkKeyPairsWorkWellUseCase: CheckKeyPairsWorkWellUseCase,
    private val getPartnerInfo2UseCase: GetPartnerInfo2UseCase,
    private val breakUpCoupleUseCase2: BreakUpCoupleUseCase2,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
    private val leaveFeeltalkUseCase: LeaveFeeltalkUseCase
) : ViewModel() {

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _myCoupleCode = MutableStateFlow("")
    val myCoupleCode = _myCoupleCode.asStateFlow()

    private val _partnerCoupleCodeInput = MutableStateFlow("")
    val partnerCoupleCodeInput = _partnerCoupleCodeInput.asStateFlow()

    private val _isKeyPairExchangingCompleted = MutableStateFlow(false)
    val isKeyPairExchangingCompleted = _isKeyPairExchangingCompleted.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    init {
        getCoupleRegistrationCode()
        collectIsCoupleRegistrationCompleted()
    }


    private fun collectIsCoupleRegistrationCompleted() = viewModelScope.launch(Dispatchers.IO) {
//        CreateCoupleObserver.getInstance()
//            .isCoupleRegistrationCompleted.collectLatest { isCompleted ->
//                if (isCompleted) {
//                    exchangeKeyPair()
//                }
//            }
    }

    private fun getCoupleRegistrationCode() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleRegistrationCodeUseCase()
        when (result) {
            is Resource.Success -> _myCoupleCode.value = result.data
            is Resource.Error -> {
                infoLog("Fail to get couple registration code: ${result.throwable.localizedMessage}")
                sendToast("내 초대코드를 불러오는데 실패했습니다")
            } else -> { }
        }
    }


    suspend fun sendPartnerCode() = viewModelScope.launch(Dispatchers.IO) {
        val partnerCode = _partnerCoupleCodeInput.value.trim()
        if (_myCoupleCode.value == partnerCode) return@launch

        _isLoading.value = true

        val result = sendPartnerCoupleRegistrationCodeUseCase(partnerCode)
        when (result) {
            is Resource.Success -> {
                val isValid = result.data.isValidCoupleCode
                if (isValid) {
                    removeCoupleRegistrationCodeUseCase()
                    exchangeKeyPair()
                } else {
                    sendToast("올바르지 않은 초대코드입니다")
                    _isLoading.value = false
                }
            }
            is Resource.Error -> {
                sendToast("초대코드 확인에 에러가 발생했습니다")
                infoLog("send partner code error: ${result.throwable.localizedMessage ?: "Unexpected error is occurred."}")
                _isLoading.value = false
            }
            else -> {
                _isLoading.value = false
            }
        }
    }

    fun exchangeKeyPair() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.value = true

        val myPublicKeyResult = uploadMyPublicKeyUseCase()
        if (myPublicKeyResult is Resource.Error) {
            sendToast("실패했습니다. 상대방의 네트워크 상태를 확인해주세요.")
            infoLog("Fail to upload MyPublicKey : ${myPublicKeyResult.throwable.localizedMessage}")
            breakUpCouple()
            reloadCoupleRegistrationCode()
//            CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
            _isLoading.value = false
            return@launch
        }

        val partnerPublicKeyResult = loadPartnerPublicKeyUseCase()
        if (partnerPublicKeyResult is Resource.Error) {
            sendToast("실패했습니다. 상대방의 네트워크 상태를 확인해주세요.")
            infoLog("Fail to load PartnerPublicKey : ${partnerPublicKeyResult.throwable.localizedMessage}")
            breakUpCouple()
            reloadCoupleRegistrationCode()
//            CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
            _isLoading.value = false
            return@launch
        }

        val myPrivateKeyResult = uploadMyPrivateKeyUseCase()
        if (myPrivateKeyResult is Resource.Error) {
            sendToast("실패했습니다. 상대방의 네트워크 상태를 확인해주세요.")
            infoLog("Fail to upload MyPrivateKey : ${myPrivateKeyResult.throwable.localizedMessage}")
            breakUpCouple()
            reloadCoupleRegistrationCode()
//            CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
            _isLoading.value = false
            return@launch
        }

        val partnerPrivateKeyResult = loadPartnerPrivateKeyUseCase()
        if (partnerPrivateKeyResult is Resource.Error) {
            sendToast("실패했습니다. 상대방의 네트워크 상태를 확인해주세요.")
            infoLog("Fail to load PartnerPrivateKey : ${partnerPrivateKeyResult.throwable.localizedMessage}")
            breakUpCouple()
            reloadCoupleRegistrationCode()
//            CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
            _isLoading.value = false
            return@launch
        }
        
        val keyPairsExist = checkKeyPairsExistUseCase()
        if (keyPairsExist is Resource.Error) {
            sendToast("실패했습니다. 상대방의 네트워크 상태를 확인해주세요.")
            infoLog("Exchanged Key Pairs are not saved : ${keyPairsExist.throwable.localizedMessage}")
            breakUpCouple()
            reloadCoupleRegistrationCode()
//            CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
            _isLoading.value = false
            return@launch
        }

        val keyPairsWorkWell = checkKeyPairsWorkWellUseCase()
        if (keyPairsWorkWell is Resource.Error) {
            sendToast("실패했습니다. 상대방의 네트워크 상태를 확인해주세요.")
            infoLog("Exchanged Key Pairs are corrupted : ${keyPairsWorkWell.throwable.localizedMessage}")
            breakUpCouple()
            reloadCoupleRegistrationCode()
//            CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
            _isLoading.value = false
            return@launch
        }

        val partnerInfoResult = getPartnerInfo2UseCase()
        if (partnerInfoResult is Resource.Error) {
            infoLog("Fail to get partner info: ${partnerInfoResult.throwable.localizedMessage}")
        }

        registerNewUserMixpanel()

        _isLoading.value = false
        _isKeyPairExchangingCompleted.value = true
//        CreateCoupleObserver.getInstance().setCoupleRegistrationCompleted(false)
    }

    private suspend fun breakUpCouple() {
        val result = breakUpCoupleUseCase2()
        when (result) {
            is Resource.Success -> {
                infoLog("Success to break up couple caused by Fail To Exchange KeyPair")
            }
            is Resource.Error -> {
                infoLog("Fail to break up couple caused by Fail To Exchange KeyPair: ${result.throwable.localizedMessage}")
            }
            else -> {
                infoLog("Fail to break up couple caused by Fail To Exchange KeyPair")
            }
        }
    }

    private suspend fun reloadCoupleRegistrationCode() {
        val result = getCoupleRegistrationCodeUseCase(withCache = false)
        when (result) {
            is Resource.Success -> {
                _myCoupleCode.value = result.data
                infoLog("Success to reload couple registration code: ${result.data}")
            }
            is Resource.Error -> {
                _myCoupleCode.value = ""
                infoLog("Fail to reload couple registration code: ${result.throwable.localizedMessage}")
            }
            else -> {
                _myCoupleCode.value = ""
                infoLog("Fail to reload couple registration code")
            }
        }
    }


    fun setPartnerCodeInput(input: String) {
        _partnerCoupleCodeInput.value = input
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toastMessage.emit(message)
    }

    suspend fun leaveFeeltalk() = withContext(Dispatchers.IO) {
        val result = leaveFeeltalkUseCase()
        when (result) {
            is Resource.Success -> {
                result.data
            }
            is Resource.Error -> {
                infoLog("Fail to leave feeltalk: ${result.throwable.localizedMessage}")
                false
            }
            else -> {
                infoLog("Fail to leave feeltalk")
                false
            }
        }
    }



    private fun registerNewUserMixpanel() = CoroutineScope(Dispatchers.IO).launch {
        val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val feeltalkDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val mixpanelDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val feeltalkBirthDate = feeltalkDateFormat.parse(userInfo.data.birth)
        val mixpanelBirthDate = (feeltalkBirthDate ?: Date()).let { mixpanelDateFormat.format(it) }

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.track("Register New User", JSONObject().apply {
            put("registrationDate", mixpanelDateFormat.format(Date()))
        })
        mixpanel.people.set(JSONObject().apply {
            put("nickname", userInfo.data.nickname)
            put("gender", userInfo.data.gender)
            put("birth", mixpanelBirthDate)
        })
    }




    override fun onCleared() {
        super.onCleared()
        CreateCoupleObserver.onCleared()
    }
}