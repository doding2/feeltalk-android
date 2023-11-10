package com.clonect.feeltalk.new_domain.model.signal

data class ChangeMySignalResponse(
    val index: Long,
    val pageIndex: Long,
    val isRead: Boolean,
    val createAt: String,
)