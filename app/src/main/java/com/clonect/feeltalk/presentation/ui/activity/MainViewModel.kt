package com.clonect.feeltalk.presentation.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.AutoLogInWithGoogleUseCase
import com.clonect.feeltalk.domain.usecase.user.CheckUserInfoIsEnteredUseCase
import com.clonect.feeltalk.domain.usecase.user.CheckUserIsCoupleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val autoLogInWithGoogleUseCase: AutoLogInWithGoogleUseCase,
    private val checkUserInfoIsEnteredUseCase: CheckUserInfoIsEnteredUseCase,
    private val checkUserIsCoupleUseCase: CheckUserIsCoupleUseCase,
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isUserInfoEntered = MutableStateFlow(false)
    val isUserInfoEntered = _isUserInfoEntered.asStateFlow()

    private val _isUserCouple = MutableStateFlow(false)
    val isUserCouple = _isUserCouple.asStateFlow()


    init {

    }


    fun autoGoogleLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (autoLogInWithGoogleUseCase()) {
            is Resource.Success -> {
                _isLoggedIn.value = true
                checkUserInfoIsEntered()
            }
            else -> {
                _isLoggedIn.value = false
                setReady()
            }
        }
    }



    private fun checkUserInfoIsEntered()= viewModelScope.launch(Dispatchers.IO) {
        val result = checkUserInfoIsEnteredUseCase()
        val isEntered = when (result) {
            is Resource.Success -> result.data
            else -> false
        }
        _isUserInfoEntered.value = isEntered

        if (isEntered) {
            checkUserIsCouple()
        } else {
            setReady()
        }
    }

    private fun checkUserIsCouple()= viewModelScope.launch(Dispatchers.IO) {
        when (val result = checkUserIsCoupleUseCase()) {
            is Resource.Success -> _isUserCouple.value = result.data.isMatch
            else -> _isUserCouple.value = false
        }
        setReady()
    }


    fun setReady() {
        _isReady.value = true
    }

}