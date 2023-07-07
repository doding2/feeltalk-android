package com.clonect.feeltalk.new_domain.model.token

import com.clonect.feeltalk.new_domain.model.user.SocialType

data class SocialToken(
    val type: SocialType,
    val email: String? = null,
    val name: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val idToken: String? = null,
    val serverAuthCode: String? = null,
    val state: String? = null
)
