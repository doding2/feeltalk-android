package com.clonect.feeltalk.domain.model.data.user

data class UserInfo(
    var name: String? = null,
    var nickname: String = "",
    var email: String? = null,
    var age: Long = 0,
    var birth: String = "",
    var emotion: Emotion = Emotion.Happy
): java.io.Serializable
