package com.clonect.feeltalk.release_presentation.ui.animatedSignUpNavigation.authAgreement

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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