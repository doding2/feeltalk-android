package com.clonect.feeltalk.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(

): ViewModel() {
    private val _isSignInSuccessState = MutableStateFlow(false)
    val isSignInSuccessState = _isSignInSuccessState.asStateFlow()

    fun signInWithGoogle(idToken: String) = viewModelScope.launch(Dispatchers.IO) {

    }

}