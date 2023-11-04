package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfoDto

fun PartnerInfoDto.toPartnerInfo(): PartnerInfo {
    return PartnerInfo(
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