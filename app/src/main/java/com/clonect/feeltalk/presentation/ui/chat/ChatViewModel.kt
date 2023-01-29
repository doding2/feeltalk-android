package com.clonect.feeltalk.presentation.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.chat.Chat
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.usecase.GetChatListUseCase
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
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _userInfoState = MutableStateFlow(UserInfo())
    val userInfoState = _userInfoState.asStateFlow()

    private val _questionState = MutableStateFlow(Question())
    val questionState = _questionState.asStateFlow()

    private val _chatListState = MutableStateFlow<MutableList<Chat>>(mutableListOf())
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
        collectNewFcmChat()
//        getUserInfo()
        getChatList()
    }

    // TODO 나중에는 서버로 보내게 수정
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
            addNewChat(it)
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

    private fun collectNewFcmChat() = viewModelScope.launch(Dispatchers.IO) {
        FirebaseCloudMessagingService.FcmNewChatObserver.Instance.newChat.collect {
            if (it is Resource.Success) {
                _chatListState.value.add(it.data)
            }
        }
    }

    private fun getChatList() = viewModelScope.launch(Dispatchers.IO) {
        getChatListUseCase().collect { result ->
            when (result) {
                is Resource.Success -> updateChatList(result.data)
                is Resource.Error -> _dialogEvent.emit(result.throwable.localizedMessage ?: "Unexpected error occurred.")
                is Resource.Loading -> {}
                else -> {}
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

    private fun addNewChat(chat: Chat) {
        _chatListState.value.add(chat)
    }


    override fun onCleared() {
        super.onCleared()
        FeeltalkApp.setQuestionIdOfShowingChatFragment(null)
    }
}