package com.clonect.feeltalk.release_domain.model.token

data class RenewTokenDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
)
