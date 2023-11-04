package com.clonect.feeltalk.new_domain.model.challenge

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class Challenge(
    val index: Long,
    val title: String,
    val body: String,
    val deadline: Date,
    val owner: String,
    val isCompleted: Boolean,
    val isNew: Boolean = false
): Serializable, Parcelable
