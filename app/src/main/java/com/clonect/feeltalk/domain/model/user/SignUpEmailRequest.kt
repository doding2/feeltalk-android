package com.clonect.feeltalk.domain.model.user

data class SignUpEmailRequest(
    val email: String,
    val password: String,
    val name: String,
    val nickname: String,
    val age: Int,
    val phone: Int,
    val accessToken: String = "",
    val refreshToken: String = "",
    val coupleid: Int = 0
)