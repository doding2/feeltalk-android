package com.clonect.feeltalk.new_presentation.ui.util

fun String.toBytesInt(): Int {
    val bytes = encodeToByteArray()
    var result = 0
    for (i in bytes.indices) {
        result = result or (bytes[i].toInt() shl 8 * i)
    }
    return result
}