package com.clonect.feeltalk.data.utils

import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink

class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
    override fun contentType(): MediaType = "image/png".toMediaType()
    override fun writeTo(sink: BufferedSink) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, sink.outputStream())
    }
}