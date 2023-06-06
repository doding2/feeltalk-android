package com.clonect.feeltalk.domain.model.dto.user

data class OldSignUpDto(
    val token: String,
    val validCode: String?,
    val annotation: String
): java.io.Serializable
