package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.usecase.account.GetMyInfoUseCase
import com.clonect.feeltalk.new_domain.usecase.partner.GetPartnerInfoFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalFlowUseCase
import com.clonect.feeltalk.new_presentation.ui.util.mutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/10/25.
 */
@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getPartnerInfoFlowUseCase: GetPartnerInfoFlowUseCase,
    private val getMySignalUseCase: GetMySignalUseCase,
    private val getPartnerSignalFlowUseCase: GetPartnerSignalFlowUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _imageChat = MutableStateFlow<ImageChat?>(null)
    val imageChat = _imageChat.asStateFlow()

    private val _myInfo: MutableStateFlow<MyInfo?> = MutableStateFlow(null)
    val myInfo = _myInfo.asStateFlow()

    private val _partnerInfo: MutableStateFlow<PartnerInfo?> = MutableStateFlow(null)
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _mySignal: MutableStateFlow<Signal> = MutableStateFlow(Signal.One)
    val mySignal = _mySignal.asStateFlow()

    private val _partnerSignal: MutableStateFlow<Signal> = MutableStateFlow(Signal.One)
    val partnerSignal = _partnerSignal.asStateFlow()

    init {
        getMyInfo()
        getPartnerInfoFlow()
        getMySignal()
        getPartnerSignalFlow()
    }


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }

    fun setImageChat(imageChat: ImageChat?) {
        _imageChat.value = imageChat
    }


    fun getMyInfo() = viewModelScope.launch {
        getMyInfoUseCase()
            .onSuccess { _myInfo.value = it }
            .onError { it.localizedMessage?.let { it1 -> sendErrorMessage(it1) } }
    }

    fun getPartnerInfoFlow() = viewModelScope.launch {
        getPartnerInfoFlowUseCase()
            .collectLatest { result ->
                result.onSuccess {
                    _partnerInfo.value = it
                }.onError {
                    it.localizedMessage?.let { it1 -> sendErrorMessage(it1) }
                }
            }
    }

    fun getMySignal() = viewModelScope.launch {
        getMySignalUseCase()
            .onSuccess { _mySignal.value = it }
            .onError { it.localizedMessage?.let { it1 -> sendErrorMessage(it1) } }
    }

    fun getPartnerSignalFlow() = viewModelScope.launch {
        getPartnerSignalFlowUseCase()
            .collectLatest {
                _partnerSignal.value = it ?: Signal.One
            }
    }


    fun downloadImage(onComplete: () -> Unit) {
        setLoading(true)
        onComplete()
        setLoading(false)
    }

}