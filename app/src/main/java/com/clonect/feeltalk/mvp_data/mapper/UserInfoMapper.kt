package com.clonect.feeltalk.mvp_data.mapper

import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo
import com.clonect.feeltalk.mvp_domain.model.dto.user.UserInfoDto

fun UserInfoDto.toUserInfo(): UserInfo {
    val birth = this.birth.substringBefore("T").replace("-", "/")
    return UserInfo(
        gender = gender,
        name = name,
        nickname = nickname,
        email = email,
        age = age,
        birth = birth,
        emotion = emotion.toEmotion()
    )
}