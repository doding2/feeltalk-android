package com.clonect.feeltalk.new_domain.model.signIn

data class ReLogInDto(
    val signUpState: String,
    val accessToken: String?,
    val refreshToken: String?,
    val expiresIn: Int?
): java.io.Serializable
