package com.clonect.feeltalk.mvp_presentation.ui.key_restoring_request

import com.clonect.feeltalk.mvp_domain.model.data.user.Emotion

data class KeyPairsState(
    val message: String,
    val state: Emotion
)
