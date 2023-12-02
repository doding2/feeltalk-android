package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation

data class AuthCodeState(
    val isAgreementDisagreed: Boolean = false,
    val isTimeOut: Boolean = false,
    val isRequested: Boolean = false,
    val isAuthCodeInvalid: Boolean = false
)
