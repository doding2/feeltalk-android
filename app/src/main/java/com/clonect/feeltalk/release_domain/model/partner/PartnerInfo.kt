package com.clonect.feeltalk.release_domain.model.partner

import com.clonect.feeltalk.release_domain.model.account.SocialType
import java.io.Serializable

data class PartnerInfo(
    val nickname: String,
    val snsType: SocialType
): Serializable
