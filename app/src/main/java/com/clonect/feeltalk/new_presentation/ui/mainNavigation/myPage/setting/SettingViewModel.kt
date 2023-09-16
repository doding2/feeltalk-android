package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.appSettings.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(

): ViewModel() {

    private val _lockEnabled = MutableStateFlow(false)
    val lockEnabled = _lockEnabled.asStateFlow()

    private val _language = MutableStateFlow<Language>(Language.Korean)
    val language = _language.asStateFlow()


    fun setLockEnabled(enabled: Boolean) {
        _lockEnabled.value = enabled
    }

    fun setLanguage(language: Language) {
        _language.value = language
    }

}