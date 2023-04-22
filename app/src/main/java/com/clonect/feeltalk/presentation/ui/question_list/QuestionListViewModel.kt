package com.clonect.feeltalk.presentation.ui.question_list

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

    init {
        collectQuestionList()
    }

    private fun collectQuestionList() = viewModelScope.launch(Dispatchers.IO) {
        getQuestionListUseCase().collect { result ->
            when (result) {
                is Resource.Success -> {
                    val newList = result.data.toMutableList()
                    newList.sortByDescending { it.questionDate }
                    newList.sortByDescending { it.question }
                    newList.add(0, Question(
                        question = "",
                        viewType = "header"
                    ))
                    _questionListState.value = newList
                }
                is Resource.Error -> { infoLog("Fail to get question list: ${result.throwable.localizedMessage}") }
                else -> {}
            }
        }
    }


}