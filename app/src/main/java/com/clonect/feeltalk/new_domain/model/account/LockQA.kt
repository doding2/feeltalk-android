package com.clonect.feeltalk.new_domain.model.account

import java.io.Serializable

data class LockQA(
    open val questionType: Int,
    open val answer: String?,
): Serializable
