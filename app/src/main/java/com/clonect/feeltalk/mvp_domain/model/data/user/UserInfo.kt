package com.clonect.feeltalk.mvp_domain.model.data.user

import java.io.Serializable

data class UserInfo(
    var gender: String? = null,
    var name: String? = null,
    var nickname: String = "",
    var email: String? = null,
    var age: Long = 0,
    var birth: String = "",
    var emotion: Emotion = Emotion.Happy
): Serializable {

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + nickname.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + age.hashCode()
        result = 31 * result + birth.hashCode()
        result = 31 * result + emotion.hashCode()
        return result
    }

}