package com.clonect.feeltalk.release_presentation.ui.passwordNavigation.partnerHelpReset

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.release_domain.model.chat.ResetPartnerPasswordChat
import com.clonect.feeltalk.release_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.release_domain.usecase.chat.SendResetPartnerPasswordChatUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext context: Context,
    private val sendResetPartnerPasswordChatUseCase: SendResetPartnerPasswordChatUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
) : ViewModel() {

    private val defaultErrorMessage = context.getString(R.string.pillowtalk_default_error_message)

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
                sendErrorMessage(defaultErrorMessage)
            }
        setLoading(false)
    }
}