package com.clonect.feeltalk.presentation.ui.question_list

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.usecase.question.GetQuestionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor(
    private val getQuestionListUseCase: GetQuestionListUseCase
) : ViewModel() {

    private val _questionListState = MutableStateFlow(listOf(Question(id = -50505L)))
    val questionListState: StateFlow<List<Question>> = _questionListState.asStateFlow()

    private val _listState = MutableStateFlow<Parcelable?>(null)
    val listState = _listState.asStateFlow()

    init {
        getQuestionList()
    }

    private fun getQuestionList() = viewModelScope.launch(Dispatchers.IO) {
        getQuestionListUseCase().collect {
            when (it) {
                is Resource.Success -> {
                    val newList = it.data.toMutableList()
                    newList.add(Question(id = -50505L))
                    _questionListState.value = newList
                }
                is Resource.Error -> {}
                else -> {}
            }
        }
    }


    fun setListState(listState: Parcelable?) {
        _listState.value = listState
    }

}