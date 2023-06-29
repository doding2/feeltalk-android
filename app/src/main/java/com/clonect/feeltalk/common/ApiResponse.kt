package com.clonect.feeltalk.common

class ApiResponse<T>(
    val status: String,
    val message: String?,
    val data: T?
)