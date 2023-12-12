package com.clonect.feeltalk.new_domain.model.token

import com.clonect.feeltalk.new_domain.model.account.SocialType
import java.io.Serializable
import java.util.*

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val accessExpiresAt: Date,
    val refreshExpiresAt: Date,
    val snsType: SocialType
): Serializable
