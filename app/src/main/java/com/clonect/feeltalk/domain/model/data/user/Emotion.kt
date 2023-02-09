package com.clonect.feeltalk.domain.model.data.user

import java.io.Serializable

sealed class Emotion: Serializable {
    object Happy: Emotion()
    object Puzzling: Emotion()
    object Bad: Emotion()
    object Angry: Emotion()
}