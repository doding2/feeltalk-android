package com.clonect.feeltalk.new_domain.model.token

import com.clonect.feeltalk.new_domain.model.user.SocialType
import java.io.Serializable
import java.util.*

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Date,
    val snsType: SocialType
): Serializable
