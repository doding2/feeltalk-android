package com.clonect.feeltalk.release_presentation.ui.mainNavigation.myPage.setting.accountSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/23.
 */
@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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