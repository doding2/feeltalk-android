package com.clonect.feeltalk.mvp_domain.model.dto.user

data class UserInfoDto(
    val gender: String,
    val name: String,
    val nickname: String,
    val email: String,
    val age: Long,
    val birth: String,
    val emotion: String
)
