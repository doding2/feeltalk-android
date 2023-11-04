package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfoDto
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.account.MyInfoDto
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.appSettings.Language

fun MyInfoDto.toMyInfo(): MyInfo {
    return MyInfo(
        nickname = nickname,
        snsType = when (snsType.lowercase()) {
            "kakao" -> SocialType.Kakao
            "naver" -> SocialType.Naver
            "google" -> SocialType.Google
            "appleandroid" -> SocialType.AppleAndroid
            "appleiOS" -> SocialType.AppleIOS
            else -> SocialType.valueOf(snsType)
        }
    )
}

fun ConfigurationInfoDto.toConfigurationInfo(): ConfigurationInfo {
    return ConfigurationInfo(
        isLock = isLock,
        language = when (language.lowercase()) {
            "korean" -> Language.Korean
            "english" -> Language.English
            "japanese" -> Language.Japanese
            "chinese" -> Language.Chinese
            else -> Language.Korean
        }
    )
}