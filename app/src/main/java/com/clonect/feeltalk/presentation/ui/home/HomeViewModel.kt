package com.clonect.feeltalk.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.notification.GetFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.notification.SendFcmUseCase
import com.clonect.feeltalk.domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.domain.usecase.user.GetPartnerInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.UpdateMyEmotionUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
    private val updateMyEmotionUseCase: UpdateMyEmotionUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val sendFcmUseCase: SendFcmUseCase,
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _todayQuestion = MutableStateFlow<Question?>(null)
    val todayQuestion = _todayQuestion.asStateFlow()


    init {
        getUserInfo()
        getPartnerInfo()
        getTodayQuestion()
    }

    fun sendNotification() = viewModelScope.launch(Dispatchers.IO) {
        getFcmTokenUseCase()?.let {
//            sendFcmUseCase(it)
        }
    }

    fun changeMyEmotion(emotion: Emotion) = viewModelScope.launch(Dispatchers.IO) {
        when (updateMyEmotionUseCase(emotion)) {
            is Resource.Success -> {
                _userInfo.value = _userInfo.value.copy(emotion = emotion)
                infoLog("update")
            }
            else -> infoLog("Fail to update my emotion")
        }
    }

    private fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getUserInfoUseCase()
        when (result) {
            is Resource.Success -> _userInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner info")
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerInfoUseCase()
        when (result) {
            is Resource.Success -> _partnerInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
    }

    private fun getTodayQuestion() = viewModelScope.launch(Dispatchers.IO) {
        val result = getTodayQuestionUseCase()
        when (result) {
            is Resource.Success -> _todayQuestion.value = result.data
            is Resource.Error -> infoLog("Fail to load today question: ${result.throwable.localizedMessage}")
            is Resource.Loading -> infoLog("Fail to load today question")
        }
    }


}