package com.clonect.feeltalk.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.IOException

fun Uri?.toBitmap(context: Context): Bitmap? {
    val uri = this ?: return null
    var bitmap: Bitmap? = null
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap
}

val Uri?.fileName: String?
    get() {
        if (this == null) return null
        var fileName: String? = null
        val path = this.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }
