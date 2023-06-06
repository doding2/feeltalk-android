package com.clonect.feeltalk.common

import java.io.Serializable

class ApiResponse<T: Serializable>(
    val status: String,
    val message: String?,
    val data: T
)