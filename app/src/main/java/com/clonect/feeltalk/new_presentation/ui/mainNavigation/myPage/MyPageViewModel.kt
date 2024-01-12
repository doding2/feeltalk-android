package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.usecase.account.GetMyInfoUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.new_domain.usecase.partner.GetPartnerInfoFlowUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getPartnerInfoFlowUseCase: GetPartnerInfoFlowUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
): ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _myInfo = MutableStateFlow<MyInfo?>(null)
    val myInfo = _myInfo.asStateFlow()

    private val _partnerInfo = MutableStateFlow<PartnerInfo?>(null)
    val partnerInfo = _partnerInfo.asStateFlow()

    init {
        initMyInfo()
        collectPartnerInfo()
    }

    private fun initMyInfo() = viewModelScope.launch {
        when (val result = getMyInfoUseCase()) {
            is Resource.Success -> {
                _myInfo.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get my info: ${result.throwable.localizedMessage}")
            }
        }
    }

    private fun collectPartnerInfo() = viewModelScope.launch {
        getPartnerInfoFlowUseCase().collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    _partnerInfo.value = result.data
                }
                is Resource.Error -> {
                    infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
                }
            }
        }
    }


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }

}