package com.clonect.feeltalk.domain.model.dto.user

data class SignUpDto(
    val token: String,
    val validCode: String?,
    val annotation: String
)
