package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.partnerSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.usecase.account.GetServiceDataCountUseCase
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

/**
 * Created by doding2 on 2023/10/04.
 */
@HiltViewModel
class PartnerSettingViewModel @Inject constructor(
    private val getPartnerInfoFlowUseCase: GetPartnerInfoFlowUseCase,
    private val getServiceDataCountUseCase: GetServiceDataCountUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _partnerInfo = MutableStateFlow<PartnerInfo?>(null)
    val partnerInfo = _partnerInfo.asStateFlow()

    init {
        preloadServiceDataCount()
        collectPartnerInfo()
    }

    private fun preloadServiceDataCount() = viewModelScope.launch {
        when (val result = getServiceDataCountUseCase()) {
            is Resource.Success -> { }
            is Resource.Error -> {
                infoLog("Fail to preload service data: ${result.throwable.localizedMessage}")
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

}