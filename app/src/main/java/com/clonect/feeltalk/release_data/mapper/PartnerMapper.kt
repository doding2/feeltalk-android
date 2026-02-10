package com.clonect.feeltalk.release_data.mapper

import com.clonect.feeltalk.release_domain.model.account.SocialType
import com.clonect.feeltalk.release_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.release_domain.model.partner.PartnerInfoDto

fun PartnerInfoDto.toPartnerInfo(): PartnerInfo {
    return PartnerInfo(
        nickname = nickname,
        snsType = when (snsType.lowercase()) {
            "kakao" -> SocialType.Kakao
            "naver" -> SocialType.Naver
            "google" -> SocialType.Google
            "apple" -> SocialType.Apple
            else -> SocialType.valueOf(snsType)
        }
    )
}