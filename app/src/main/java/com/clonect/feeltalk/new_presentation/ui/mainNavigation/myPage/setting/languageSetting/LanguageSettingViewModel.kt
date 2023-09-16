package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.languageSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.new_domain.model.appSettings.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSettingViewModel @Inject constructor(

): ViewModel() {

    private val _appliedLanguage = MutableStateFlow<Language>(Language.Korean)
    val appliedLanguage = _appliedLanguage.asStateFlow()

    private val _selectedLanguage = MutableStateFlow<Language>(Language.Korean)
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _isNoticeChecked = MutableStateFlow(false)
    val isNoticeChecked = _isNoticeChecked.asStateFlow()

    private val _isChangeEnabled = MutableStateFlow(false)
    val isChangeEnabled = _isChangeEnabled.asStateFlow()



    fun setSelectedLanguage(language: Language) {
        _selectedLanguage.value = language
        computeChangeEnabled()
    }
    
    fun toggleNoticeChecked() {
        _isNoticeChecked.value = !_isNoticeChecked.value
        computeChangeEnabled()
    }

    fun computeChangeEnabled() {
        _isChangeEnabled.value = appliedLanguage.value != selectedLanguage.value && isNoticeChecked.value
    }

    fun changeLanguageSetting(onComplete: () -> Unit) = viewModelScope.launch {

    }
}