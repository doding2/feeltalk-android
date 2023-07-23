package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetPartnerLastChatUseCase
import com.clonect.feeltalk.new_presentation.service.notification_observer.NewChatObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
    private val getPartnerLastChatUseCase: GetPartnerLastChatUseCase,
): ViewModel() {

    private val _partnerLastChat = MutableStateFlow<String?>(null)
    val partnerLastChat = _partnerLastChat.asStateFlow()

    private var isArgumentsInit = true

    private val _showChatNavigation = MutableStateFlow(false)
    val showChatNavigation = _showChatNavigation.asStateFlow()

    init {
        getPartnerLastChat()
        collectNewChat()
    }
    
    private fun getPartnerLastChat() = viewModelScope.launch { 
        when (val result = getPartnerLastChatUseCase()) {
            is Resource.Success -> {
                _partnerLastChat.value = result.data.message
            }
            is Resource.Error -> {
                infoLog("연인의 가장 최근 채팅 가져오기 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun toggleShowChatNavigation() {
        _showChatNavigation.value = _showChatNavigation.value.not()
    }

    fun setShowChatNavigation(showChat: Boolean) = viewModelScope.launch {
        if (isArgumentsInit) {
            isArgumentsInit = false
            _showChatNavigation.value = showChat

            val appSettings = getAppSettingsUseCase()
            appSettings.activeChatNotification = 0
            saveAppSettingsUseCase(appSettings)
        }
    }


    private fun collectNewChat() = viewModelScope.launch {
        NewChatObserver
            .getInstance()
            .newChat
            .collectLatest { newChat ->
                runCatching {
                    val message  = when (newChat?.type) {
                        ChatType.TextChatting -> {
                            val textChat = newChat as? TextChat ?: return@collectLatest
                            textChat.message
                        }
                        ChatType.VoiceChatting -> {
                            "(보이스 채팅)"
                        }
                        else -> return@collectLatest
                    }

                    _partnerLastChat.value = message
                }.onFailure {
                    infoLog("collectNewChat(): ${it.localizedMessage}")
                }
            }
    }
}