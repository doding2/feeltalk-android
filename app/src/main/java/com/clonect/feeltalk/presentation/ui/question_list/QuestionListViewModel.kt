package com.clonect.feeltalk.presentation.ui.question_list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor() : ViewModel() {

    private val _scrollPositionState = MutableStateFlow(0)
    val scrollPositionState: StateFlow<Int> = _scrollPositionState.asStateFlow()

    fun saveScrollPosition(scrollPosition: Int) {
        _scrollPositionState.value = scrollPosition
    }
}