package com.clonect.feeltalk.presentation.ui.couple_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.user.GetPartnerInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoupleSettingViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    init {
        getUserInfo()
        getPartnerInfo()
    }

    private fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = getUserInfoUseCase()) {
            is Resource.Success -> _userInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerInfoUseCase()
        when (result) {
            is Resource.Success -> _partnerInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
    }
}