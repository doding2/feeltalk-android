package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.model.dto.user.UserInfoDto

fun UserInfoDto.toUserInfo(): UserInfo {
    val birth = this.birth.substringBefore("T").replace("-", "/")
    return UserInfo(
        name = name,
        nickname = nickname,
        email = email,
        age = age,
        birth = birth,
        emotion = emotion.toEmotion()
    )
}