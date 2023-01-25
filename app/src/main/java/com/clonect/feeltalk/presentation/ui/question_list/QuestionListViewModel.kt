package com.clonect.feeltalk.presentation.ui.question_list

import android.util.Log
import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor() : ViewModel() {

    private val _questionListState = MutableStateFlow<List<Question>>(emptyList())
    val questionListState: StateFlow<List<Question>> = _questionListState.asStateFlow()

    private val _scrollPositionState = MutableStateFlow(0)
    val scrollPositionState: StateFlow<Int> = _scrollPositionState.asStateFlow()

    init {

    }

    fun saveScrollPosition(scrollPosition: Int) {
        _scrollPositionState.value = scrollPosition
    }
}