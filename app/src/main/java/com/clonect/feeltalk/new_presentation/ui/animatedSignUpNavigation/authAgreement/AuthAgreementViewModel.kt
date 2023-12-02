package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation.authAgreement

import com.clonect.feeltalk.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/12/02.
 */
@HiltViewModel
class AuthAgreementViewModel @Inject constructor(

) : ViewModel() {

    private val _state: MutableStateFlow<AuthAgreementState?> = MutableStateFlow(null)
    val state = _state.asStateFlow()

    fun setState(state: AuthAgreementState) {
        _state.value = state
    }

}