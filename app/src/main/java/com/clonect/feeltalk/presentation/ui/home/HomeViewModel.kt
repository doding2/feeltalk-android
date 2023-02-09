package com.clonect.feeltalk.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.notification.GetFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.emotion.GetPartnerEmotionUseCase
import com.clonect.feeltalk.domain.usecase.encryption.TestUseCase
import com.clonect.feeltalk.domain.usecase.notification.SendFcmUseCase
import com.clonect.feeltalk.domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.UpdateMyEmotionUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateMyEmotionUseCase: UpdateMyEmotionUseCase,
    private val getPartnerEmotionUseCase: GetPartnerEmotionUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val sendFcmUseCase: SendFcmUseCase,
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _partnerEmotionState = MutableStateFlow<Emotion>(Emotion.Happy)
    val partnerEmotionState = _partnerEmotionState.asStateFlow()

    init {
        getUserInfo()
        getPartnerEmotion()
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
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Still Loading User Info")
        }
    }

    private fun getPartnerEmotion() = viewModelScope.launch(Dispatchers.IO) {
        getPartnerEmotionUseCase().collect {
            when (it) {
                is Resource.Success -> _partnerEmotionState.value = it.data
                else -> {}
            }
        }
    }


}