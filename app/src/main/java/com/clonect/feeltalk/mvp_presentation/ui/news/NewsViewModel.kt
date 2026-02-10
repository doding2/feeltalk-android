package com.clonect.feeltalk.mvp_presentation.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.news.News
import com.clonect.feeltalk.release_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.release_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.mvp_domain.usecase.news.GetNewsListUseCase
import com.clonect.feeltalk.mvp_domain.usecase.user.GetPartnerProfileImageUrlUseCase
import com.clonect.feeltalk.release_domain.model.appSettings.AppSettings
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase,
    private val getPartnerProfileImageUrlUseCase: GetPartnerProfileImageUrlUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
) : ViewModel() {

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList = _newsList.asStateFlow()

    private val _partnerProfileUrl = MutableStateFlow<String?>(null)
    val partnerProfileUrl = _partnerProfileUrl.asStateFlow()

    init {
        getNewsList()
        getPartnerProfileUrl()
    }

    private fun getNewsList() = viewModelScope.launch(Dispatchers.IO) {
        val result = getNewsListUseCase()
        when (result) {
            is Resource.Success -> _newsList.value = result.data
            is Resource.Error -> { infoLog("Fail to get news list: ${result.throwable.localizedMessage}") }
        }
    }

    private fun getPartnerProfileUrl() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerProfileImageUrlUseCase()
        when (result) {
            is Resource.Success -> _partnerProfileUrl.value = result.data
            is Resource.Error -> { infoLog("Fail to get partner profile url: ${result.throwable.localizedMessage}") }
        }
    }


    fun getAppSettings() = getAppSettingsUseCase()

    fun saveAppSettings(appSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(appSettings)
    }
}