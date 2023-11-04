package com.clonect.feeltalk.new_domain.model.partner

import com.clonect.feeltalk.new_domain.model.account.SocialType
import java.io.Serializable

data class PartnerInfo(
    val nickname: String,
    val snsType: SocialType
): Serializable
