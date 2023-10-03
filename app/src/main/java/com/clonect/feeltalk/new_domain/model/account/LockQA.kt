package com.clonect.feeltalk.new_domain.model.account

import java.io.Serializable

data class LockQA(
    val questionType: Int,
    val answer: String?,
): Serializable
