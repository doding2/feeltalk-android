package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.usecase.account.GetMyInfoUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase
): ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _myInfo = MutableStateFlow<MyInfo?>(null)
    val myInfo = _myInfo.asStateFlow()

    init {
        initMyInfo()
    }

    private fun initMyInfo() = viewModelScope.launch {
        when (val result = getMyInfoUseCase()) {
            is Resource.Success -> {
                _myInfo.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get my info: ${result.throwable.localizedMessage}")
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

}