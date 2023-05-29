package com.clonect.feeltalk.new_domain.model.question

import java.io.Serializable

data class Question(
    val index: Int,
    val header: String,
    val body: String,
    val date: String,
    var myAnswer: String? = null,
    val partnerAnswer: String? = null
): Serializable
