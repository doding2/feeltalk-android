package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_presentation.ui.util.mutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/10/25.
 */
@HiltViewModel
class ImageDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _imageChat = MutableStateFlow<ImageChat?>(null)
    val imageChat = _imageChat.asStateFlow()

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }

    fun setImageChat(imageChat: ImageChat?) {
        _imageChat.value = imageChat
    }


    fun downloadImage(onComplete: () -> Unit) {
        setLoading(true)
        onComplete()
        setLoading(false)
    }

}