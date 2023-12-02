package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation.authAgreement

data class AuthAgreementState(
    val isPrivacyPolicyAgreed: Boolean = false,
    val isIdentificationInfoAgreed: Boolean = false,
    val isServiceUsageAgreed: Boolean = false,
    val isMobileCarrierUsageAgreed: Boolean = false
)
