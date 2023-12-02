package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation

sealed class AnimatedSignUpState {
    object Start: AnimatedSignUpState()
    object Default: AnimatedSignUpState()
    object Name: AnimatedSignUpState()
    object Birth: AnimatedSignUpState()
    object Gender: AnimatedSignUpState()
    object MobileCarrier: AnimatedSignUpState()
    object PhoneNumber: AnimatedSignUpState()
    object Agreement: AnimatedSignUpState()
    object AuthCodeReady: AnimatedSignUpState()
    object AuthCode: AnimatedSignUpState()
    object AuthCodeError: AnimatedSignUpState()
}