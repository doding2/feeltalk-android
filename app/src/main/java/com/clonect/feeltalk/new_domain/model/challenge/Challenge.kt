package com.clonect.feeltalk.new_domain.model.challenge

import java.io.Serializable
import java.util.*

data class Challenge(
    val id: Long,
    val category: String,
    val title: String,
    val content: String? = null,
    val deadline: Date,
    val owner: String,
    val isCompleted: Boolean
): Serializable
