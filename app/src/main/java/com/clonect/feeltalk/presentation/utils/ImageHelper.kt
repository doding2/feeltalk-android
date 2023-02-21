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
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap?.resize()
}

fun Bitmap.resize(size: Int = 512): Bitmap {
    var image = this
    if (size > 0) {
        val width = image.width
        val height = image.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = size.toFloat() / size.toFloat()

        var finalWidth = size
        var finalHeight = size
        if (ratioMax > ratioBitmap) {
            finalWidth = (size.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (size.toFloat() / ratioBitmap).toInt()
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
    }

    return image
}