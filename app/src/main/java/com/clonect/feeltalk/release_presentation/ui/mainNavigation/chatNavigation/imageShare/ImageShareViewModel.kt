package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.imageShare

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/10/24.
 */
@HiltViewModel
class ImageShareViewModel @Inject constructor(
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri = _uri.asStateFlow()

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setUri(uri: Uri?) {
        _uri.value = uri
    }

    fun setBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }
}