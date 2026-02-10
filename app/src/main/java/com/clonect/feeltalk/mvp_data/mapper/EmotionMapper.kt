package com.clonect.feeltalk.mvp_data.mapper

import com.clonect.feeltalk.mvp_domain.model.data.user.Emotion

fun String.toEmotion(): Emotion = when (this.lowercase()) {
    "happy" -> Emotion.Happy
    "puzzling" -> Emotion.Puzzling
    "bad" -> Emotion.Bad
    "angry" -> Emotion.Angry
    else -> Emotion.Happy
}

fun Emotion.toStringLowercase(): String = when (this) {
    is Emotion.Happy -> "happy"
    is Emotion.Puzzling -> "puzzling"
    is Emotion.Bad -> "bad"
    is Emotion.Angry -> "angry"
}