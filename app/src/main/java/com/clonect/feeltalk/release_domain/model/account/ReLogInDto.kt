package com.clonect.feeltalk.release_domain.model.account

data class ReLogInDto(
    val signUpState: String,
    val accessToken: String?,
    val refreshToken: String?,
    val expiresIn: Int?
): java.io.Serializable
