package com.clonect.feeltalk.new_domain.model.challenge

import java.io.Serializable
import java.util.*

data class Challenge(
    val index: Long,
    val category: ChallengeCategory,
    val title: String,
    val body: String,
    val deadline: Date,
    val owner: String,
    val isCompleted: Boolean
): Serializable
