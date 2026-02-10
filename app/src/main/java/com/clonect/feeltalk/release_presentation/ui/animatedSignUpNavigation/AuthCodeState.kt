package com.clonect.feeltalk.release_presentation.ui.animatedSignUpNavigation

data class AuthCodeState(
    val isAgreementDisagreed: Boolean = false,
    val isTimeOut: Boolean = false,
    val isRequested: Boolean = false,
    val isAuthCodeInvalid: Boolean = false
)
