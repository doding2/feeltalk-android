package com.clonect.feeltalk.new_presentation.ui.passwordNavigation.partnerHelpReset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.model.chat.ResetPartnerPasswordChat
import com.clonect.feeltalk.new_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendResetPartnerPasswordChatUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/09/20.
 */
@HiltViewModel
class PartnerHelpResetViewModel @Inject constructor(
    private val sendResetPartnerPasswordChatUseCase: SendResetPartnerPasswordChatUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
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


    fun sendRequestChat(onSuccess: () -> Unit) = viewModelScope.launch {
        setLoading(true)
        sendResetPartnerPasswordChatUseCase()
            .onSuccess {
                addNewChatCacheUseCase(
                    ResetPartnerPasswordChat(
                        index = it.index,
                        pageNo = it.pageIndex,
                        chatSender = "me",
                        isRead = it.isRead,
                        createAt = it.createAt,
                    )
                )
                onSuccess()
            }
            .onError {
                infoLog("Fail to send request chat: ${it.localizedMessage}")
                it.localizedMessage?.let { it1 -> sendErrorMessage(it1) }
            }
        setLoading(false)
    }
}