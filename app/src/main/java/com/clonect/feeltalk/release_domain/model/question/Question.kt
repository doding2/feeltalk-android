package com.clonect.feeltalk.release_domain.model.question

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
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
): Serializable, Parcelable
