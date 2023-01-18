package com.clonect.feeltalk.domain.model.user

import java.io.File

data class SignUpEmailRequest(
    val email: String,
    val password: String,
    val name: String,
    val nickname: String,
    val age: String,
    val phone: String,
    val profile: File
)