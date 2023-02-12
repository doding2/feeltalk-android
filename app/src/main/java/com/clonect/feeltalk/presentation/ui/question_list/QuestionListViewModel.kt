package com.clonect.feeltalk.presentation.ui.question_list

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.usecase.question.GetQuestionListUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor(
    private val getQuestionListUseCase: GetQuestionListUseCase
) : ViewModel() {

    private val _questionListState = MutableStateFlow(listOf(Question(question = "", viewType = "header")))
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
                    newList.add(Question(
                        question = "",
                        viewType = "header"
                    ))
                    _questionListState.value = newList
                }
                is Resource.Error -> { infoLog("Fail to get question list: ${it.throwable.localizedMessage}") }
                else -> {}
            }
        }
    }


    fun setListState(listState: Parcelable?) {
        _listState.value = listState
    }

}