package com.clonect.feeltalk.new_domain.model.question

import java.io.Serializable

data class Question(
    val index: Long,
    val pageNo: Long,
    val title: String,
    val header: String,
    val body: String,
    val highlight: List<Long>,
    val createAt: String,
    var myAnswer: String?,
    val partnerAnswer: String?,
): Serializable
