package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockSettingViewModel @Inject constructor(

): ViewModel() {

    private val _lockEnabled = MutableStateFlow(false)
    val lockEnabled = _lockEnabled.asStateFlow()


    init {

    }

    fun setLockEnabled(enabled: Boolean) = viewModelScope.launch {
        _lockEnabled.value = enabled
    }



}