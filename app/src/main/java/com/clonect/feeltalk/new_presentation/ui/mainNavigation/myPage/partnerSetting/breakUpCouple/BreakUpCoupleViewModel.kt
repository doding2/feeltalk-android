package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.partnerSetting.breakUpCouple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.ServiceDataCountDto
import com.clonect.feeltalk.new_domain.usecase.account.BreakUpCoupleUseCase
import com.clonect.feeltalk.new_domain.usecase.account.GetServiceDataCountUseCase
import com.clonect.feeltalk.new_presentation.service.FirebaseCloudMessagingService
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/21.
 */
@HiltViewModel
class BreakUpCoupleViewModel @Inject constructor(
    private val getServiceDataCountUseCase: GetServiceDataCountUseCase,
    private val breakUpCoupleUseCase: BreakUpCoupleUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _serviceDataCount = MutableStateFlow<ServiceDataCountDto?>(null)
    val serviceDataCount = _serviceDataCount.asStateFlow()

    private val _isAllAgreed = MutableStateFlow(false)
    val isAllAgreed = _isAllAgreed.asStateFlow()


    init {
        initServiceDataCount()
    }

    private fun initServiceDataCount() = viewModelScope.launch {
        when (val result = getServiceDataCountUseCase()) {
            is Resource.Success -> {
                _serviceDataCount.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get service data count: ${result.throwable.localizedMessage}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
    }

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }


    fun toggleIsAllAgreed() {
        _isAllAgreed.value = _isAllAgreed.value.not()
    }



    fun breakUpCouple(onComplete: () -> Unit) = viewModelScope.launch {
        setLoading(true)
        when (val result = breakUpCoupleUseCase()) {
            is Resource.Success -> {
                FirebaseCloudMessagingService.clearFcmToken()
                onComplete()
            }
            is Resource.Error -> {
                infoLog("Fail to break up couple: ${result.throwable.localizedMessage}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
        setLoading(false)
    }

}