package com.clonect.feeltalk.mvp_presentation.ui.question_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import com.clonect.feeltalk.mvp_domain.usecase.question.GetQuestionListUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
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

    private val _question2ListState = MutableStateFlow(listOf(Question2(question = "", viewType = "header")))
    val question2ListState: StateFlow<List<Question2>> = _question2ListState.asStateFlow()

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
                    newList.add(0, Question2(
                        question = "",
                        viewType = "header"
                    ))
                    _question2ListState.value = newList
                }
                is Resource.Error -> { infoLog("Fail to get question list: ${result.throwable.localizedMessage}") }
                else -> {}
            }
        }
    }


}