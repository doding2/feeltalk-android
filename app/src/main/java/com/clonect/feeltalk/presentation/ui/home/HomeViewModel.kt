package com.clonect.feeltalk.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.Emotion
import com.clonect.feeltalk.domain.usecase.notification.GetFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.emotion.GetMyEmotionUseCase
import com.clonect.feeltalk.domain.usecase.emotion.GetPartnerEmotionUseCase
import com.clonect.feeltalk.domain.usecase.encryption.TestUseCase
import com.clonect.feeltalk.domain.usecase.notification.SendFcmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyEmotionUseCase: GetMyEmotionUseCase,
    private val getPartnerEmotionUseCase: GetPartnerEmotionUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val sendFcmUseCase: SendFcmUseCase,
    private val testUseCase: TestUseCase
): ViewModel() {

    private val _myEmotionState = MutableStateFlow<Emotion>(Emotion.Happy)
    val myEmotionState = _myEmotionState.asStateFlow()

    private val _partnerEmotionState = MutableStateFlow<Emotion>(Emotion.Happy)
    val partnerEmotionState = _partnerEmotionState.asStateFlow()

    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            testUseCase()
//        }
        getMyEmotion()
        getPartnerEmotion()
    }

    fun sendNotification() = viewModelScope.launch(Dispatchers.IO) {
        getFcmTokenUseCase()?.let {
            sendFcmUseCase(it)
        }
    }

    fun changeMyEmotion(emotion: Emotion) {
        _myEmotionState.value = emotion
    }


    private fun getMyEmotion() = viewModelScope.launch(Dispatchers.IO) {
        getMyEmotionUseCase().collect {
            when (it) {
                is Resource.Success -> _myEmotionState.value = it.data
                else -> {}
            }
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