package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageShare

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.new_presentation.ui.util.mutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/10/24.
 */
@HiltViewModel
class ImageShareViewModel @Inject constructor(

) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    var isLoading: Boolean by mutableStateFlow(false)

    var uri: Uri? by mutableStateFlow(null)

    var image: Bitmap? by mutableStateFlow(null)


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


    fun sendImageChat(onComplete: () -> Unit) = viewModelScope.launch {
        isLoading = true
        onComplete()
        isLoading = false
    }
}