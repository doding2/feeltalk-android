package com.clonect.feeltalk.presentation.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.news.News
import com.clonect.feeltalk.domain.usecase.GetNewsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase
) : ViewModel() {

    private val _newsState = MutableStateFlow<List<News>>(emptyList())
    val newsState = _newsState.asStateFlow()

    init {
        getNewsList()
    }

    private fun getNewsList() = viewModelScope.launch(Dispatchers.IO) {
        getNewsListUseCase().collect {
            when {
                it is Resource.Success -> _newsState.value = it.data
                it is Resource.Error -> { }
            }
        }
    }
}