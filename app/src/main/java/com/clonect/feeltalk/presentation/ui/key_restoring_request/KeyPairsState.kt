package com.clonect.feeltalk.presentation.ui.key_restoring_request

import com.clonect.feeltalk.domain.model.data.user.Emotion

data class KeyPairsState(
    val message: String,
    val state: Emotion
)
