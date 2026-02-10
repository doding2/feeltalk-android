package com.clonect.feeltalk.release_presentation.ui.mainNavigation.home.signal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Point
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.release_domain.model.chat.Chat
import com.clonect.feeltalk.release_domain.model.chat.SignalChat
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.release_domain.usecase.signal.ChangeMySignalUseCase
import com.clonect.feeltalk.release_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignalViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getMySignalUseCase: GetMySignalUseCase,
    private val changeMySignalUseCase: ChangeMySignalUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
) : ViewModel() {

    private val defaultErrorMessage = context.getString(R.string.pillowtalk_default_error_message)

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signal = MutableStateFlow<Signal?>(null)
    val signal = _signal.asStateFlow()

    init {
        getMySignal()
    }

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }


    fun getMySignal(onComplete: (Signal) -> Unit = {}) = viewModelScope.launch {
        getMySignalUseCase()
            .onSuccess {
                _signal.value = it
                onComplete(it)
            }
            .onError { infoLog("Fail to get my signal: ${it.localizedMessage}") }
    }


    fun setSignal(signal: Signal) {
        _signal.value = signal
    }

    fun changeMySignal(onComplete: (Signal) -> Unit) = viewModelScope.launch {
        if (isLoading.value) return@launch
        val signal = signal.value ?: return@launch
        setLoading(true)
        changeMySignalUseCase(signal)
            .onSuccess {
                launch {
                    addNewChatCacheUseCase(
                        SignalChat(
                            index = it.index,
                            pageNo = it.pageIndex,
                            chatSender = "me",
                            isRead = it.isRead,
                            createAt = it.createAt,
                            sendState = Chat.ChatSendState.Completed,
                            signal = signal
                        )
                    )
                    onComplete(signal)
                }
            }.onError {
                infoLog("Fail to change my signal: ${it.localizedMessage}")
                sendErrorMessage(defaultErrorMessage)
            }
        setLoading(false)
    }


    private val _centerPoint = MutableStateFlow<Point<Float>?>(null)
    val centerPoint = _centerPoint.asStateFlow()

    private val _diameter = MutableStateFlow<Float?>(null)
    val diameter = _diameter.asStateFlow()

    private val _angle = MutableStateFlow<Float?>(null)
    val angle = _angle.asStateFlow()


    fun setCenterPoint(point: Point<Float>) = viewModelScope.launch {
        _centerPoint.value = point
    }

    fun setDiameter(diameter: Float) = viewModelScope.launch {
        _diameter.value = diameter
    }

    fun setAngle(angle: Float) = viewModelScope.launch {
        _angle.value = angle
    }
}