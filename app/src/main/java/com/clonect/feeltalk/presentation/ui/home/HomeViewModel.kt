package com.clonect.feeltalk.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.Emotion
import com.clonect.feeltalk.domain.usecase.GetMyEmotionUseCase
import com.clonect.feeltalk.domain.usecase.GetPartnerEmotionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyEmotionUseCase: GetMyEmotionUseCase,
    private val getPartnerEmotionUseCase: GetPartnerEmotionUseCase
): ViewModel() {

    private val _myEmotionState = MutableStateFlow<Emotion>(Emotion.Happy)
    val myEmotionState = _myEmotionState.asStateFlow()

    private val _partnerEmotionState = MutableStateFlow<Emotion>(Emotion.Happy)
    val partnerEmotionState = _partnerEmotionState.asStateFlow()

    init {
        getMyEmotion()
        getPartnerEmotion()
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