package com.clonect.feeltalk.domain.model.user

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("accessToken")
    val value: String
)