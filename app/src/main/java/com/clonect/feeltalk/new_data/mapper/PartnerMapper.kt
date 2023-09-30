package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfoDto

fun PartnerInfoDto.toPartnerInfo(): PartnerInfo {
    return PartnerInfo(
        nickname = nickname
    )
}