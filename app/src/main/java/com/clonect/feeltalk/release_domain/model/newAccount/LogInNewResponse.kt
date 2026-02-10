package com.clonect.feeltalk.release_domain.model.newAccount

data class LogInNewResponse(
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String,
    val expiredTime: String,
)
