package com.clonect.feeltalk.presentation.ui.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.chat.Chat
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.presentation.service.FirebaseCloudMessagingService
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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

    private val _userInfoState = MutableStateFlow(UserInfo())
    val userInfoState = _userInfoState.asStateFlow()

    private val _questionState = MutableStateFlow(Question())
    val questionState = _questionState.asStateFlow()

    private val _chatListState = MutableStateFlow<List<Chat>>(emptyList())
    val chatListState = _chatListState.asStateFlow()

    private val _dialogEvent = MutableSharedFlow<String>()
    val dialogEvent = _dialogEvent.asSharedFlow()

    private val _scrollPositionState = MutableStateFlow(0)
    val scrollPositionState = _scrollPositionState.asStateFlow()

    init {
        savedStateHandle.get<Question>("selectedQuestion")?.let {
            _questionState.value = it
            FeeltalkApp.setQuestionIdOfShowingChatFragment(it.id)
        }
        collectFcmNewChat()
//        getUserInfo()
        getChatList()
    }

    fun sendChat(content: String) = viewModelScope.launch(Dispatchers.IO) {
        val format = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss", Locale.getDefault())
        val date = format.format(Date())

        Chat(
            id = _chatListState.value.size.toLong(),
            questionId = _questionState.value.id,
            ownerEmail = "mine",
            content = content,
            date = date
        ).also {
            sendChatUseCase(it)
        }
    }

    fun updateScrollPosition(position: Int) {
        _scrollPositionState.value = position
    }

//    private fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
//        getUserInfoUseCase().collect { result ->
//            when (result) {
//                is Resource.Success -> _userInfoState.value = result.data
//                is Resource.Error -> _dialogEvent.emit(result.throwable.localizedMessage ?: "Unexpected error occurred.")
//                else -> {}
//            }
//        }
//    }

    private fun collectFcmNewChat() = viewModelScope.launch(Dispatchers.IO) {
        FirebaseCloudMessagingService.FcmNewChatObserver.getInstance().newChat.collect {
            if (it is Resource.Success) {
                val newList = mutableListOf<Chat>().apply {
                    addAll(_chatListState.value)
                    add(it.data)
                }
                _chatListState.value = newList
            }
        }
    }

    private fun getChatList() = viewModelScope.launch(Dispatchers.IO) {
        val questionId = _questionState.value.id
        getChatListUseCase(questionId).collect { result ->
            when (result) {
                is Resource.Success -> updateChatList(result.data)
                is Resource.Error -> {
                    Log.i("ChatFragment", "getChatList() error: ${result.throwable.localizedMessage}")
                    _dialogEvent.emit(result.throwable.localizedMessage
                        ?: "Unexpected error occurred.")
                }
                is Resource.Loading -> {  }
            }
        }
    }

    private fun updateChatList(chatList: List<Chat>) {
        val currentQuestion = questionState.value
        val newList = mutableListOf<Chat>()

        if (currentQuestion.myAnswer.isNotBlank()) {
            Chat(
                id = 0L,
                questionId = _questionState.value.id,
                ownerEmail = "mine", // TODO 제대로된 정보로 변경
                content = _questionState.value.myAnswer,
                date = _questionState.value.myAnswerDate,
                isAnswer = true
            ).also {
                newList.add(it)
            }
        }
        if (currentQuestion.partnerAnswer.isNotBlank()) {
            Chat(
                id = 1L,
                questionId = _questionState.value.id,
                ownerEmail = "partner", // TODO 제대로된 정보로 변경
                content = _questionState.value.partnerAnswer,
                date = _questionState.value.partnerAnswerDate,
                isAnswer = true
            ).also {
                newList.add(it)
            }
        }

        newList.addAll(chatList)
        newList.sortBy { it.date }
        _chatListState.value = newList
    }

    override fun onCleared() {
        super.onCleared()
        FeeltalkApp.setQuestionIdOfShowingChatFragment(null)
        FirebaseCloudMessagingService.FcmNewChatObserver.onCleared()
    }
}