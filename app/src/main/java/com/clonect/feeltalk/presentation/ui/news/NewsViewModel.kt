package com.clonect.feeltalk.presentation.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.news.News
import com.clonect.feeltalk.domain.usecase.news.GetNewsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase
) : ViewModel() {

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList = _newsList.asStateFlow()

    init {
        getNewsList()
    }

    private fun getNewsList() = viewModelScope.launch(Dispatchers.IO) {
        val result = getNewsListUseCase()
        when (result) {
            is Resource.Success -> _newsList.value = result.data
            is Resource.Error -> { }
            is Resource.Loading -> { }
        }

    }
}