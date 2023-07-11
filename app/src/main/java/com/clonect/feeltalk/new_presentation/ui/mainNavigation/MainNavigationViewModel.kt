package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(): ViewModel() {

    private var isArgumentsInit = true

    private val _showChatNavigation = MutableStateFlow(false)
    val showChatNavigation = _showChatNavigation.asStateFlow()

    fun toggleShowChatNavigation() {
        _showChatNavigation.value = _showChatNavigation.value.not()
    }

    fun setShowChatNavigation(showChat: Boolean) {
        if (isArgumentsInit) {
            isArgumentsInit = false
            _showChatNavigation.value = showChat
        }
    }
}