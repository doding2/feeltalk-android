package com.clonect.feeltalk.presentation.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.presentation.service.notification_observer.FcmNewChatObserver
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatListUseCase: GetChatListUseCase,
    private val sendChatUseCase: SendChatUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _question = MutableStateFlow(Question(""))
    val question = _question.asStateFlow()

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList = _chatList.asStateFlow()

    private val _dialogEvent = MutableSharedFlow<String>()
    val dialogEvent = _dialogEvent.asSharedFlow()

    private val _scrollPositionState = MutableStateFlow(0)
    val scrollPositionState = _scrollPositionState.asStateFlow()


    init {
        savedStateHandle.get<Question>("selectedQuestion")?.let {
            _question.value = it
            FeeltalkApp.setQuestionIdOfShowingChatFragment(it.question)
            infoLog("Chat Room Entered: $it")
        }
        collectChatList()
        collectFcmNewChat()
    }

    private fun collectFcmNewChat() = viewModelScope.launch(Dispatchers.IO) {
        FcmNewChatObserver.getInstance().newChat.collect {
            if (it is Resource.Success) {
                val newList = mutableListOf<Chat>().apply {
                    addAll(_chatList.value)
                    add(it.data)
                }
                _chatList.value = newList
            }
        }
    }

    private fun collectChatList() = viewModelScope.launch(Dispatchers.IO) {
        val questionContent = _question.value.question
        getChatListUseCase(questionContent).collect { result ->
            when (result) {
                is Resource.Success -> {
                    updateChatList(result.data)
                    infoLog("Success to get chat list: ${result.data}")
                }
                is Resource.Error -> {
                    infoLog("Fail to get chat list: ${result.throwable.localizedMessage}")
                    _dialogEvent.emit(result.throwable.localizedMessage
                        ?: "Unexpected error occurred.")
                }
                is Resource.Loading -> {
                    infoLog("Fail to get chat list")
                    _isLoading.value = result.isLoading
                }
            }
        }
    }

    private fun updateChatList(chatList: List<Chat>) {
        val newList = mutableListOf<Chat>()
        newList.addAll(chatList)
        newList.sortBy { it.id }
        _chatList.value = newList
    }



    fun sendChat(content: String) = viewModelScope.launch(Dispatchers.IO) {
        val format = SimpleDateFormat("yyyy/MM/dd/hh/mm/ss", Locale.getDefault())
        val date = format.format(Date())

        val chat = Chat(
            id = _chatList.value.size.toLong(),
            question = _question.value.question,
            owner = "mine",
            message = content,
            date = date
        )

        val result = sendChatUseCase(chat)
        when (result) {
            is Resource.Success -> {
                infoLog("Success to send chat: ${result.data}")
            }
            is Resource.Error -> {
                infoLog("Fail to send chat: ${result.throwable.localizedMessage}")
            }
            else -> {
                infoLog("Fail to send chat")
            }
        }
    }


    fun updateScrollPosition(position: Int) {
        _scrollPositionState.value = position
    }


    override fun onCleared() {
        super.onCleared()
        FeeltalkApp.setQuestionIdOfShowingChatFragment(null)
        FcmNewChatObserver.onCleared()
    }
}