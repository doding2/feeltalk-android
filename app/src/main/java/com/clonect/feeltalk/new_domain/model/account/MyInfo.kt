package com.clonect.feeltalk.new_domain.model.account

import java.io.Serializable

data class MyInfo(
    val nickname: String,
    val snsType: SocialType
) : Serializable
