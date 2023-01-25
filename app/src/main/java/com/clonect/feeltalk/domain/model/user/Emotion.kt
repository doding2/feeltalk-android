package com.clonect.feeltalk.domain.model.user

sealed class Emotion {
    object Happy: Emotion()
    object Puzzling: Emotion()
    object Bad: Emotion()
    object Angry: Emotion()
}