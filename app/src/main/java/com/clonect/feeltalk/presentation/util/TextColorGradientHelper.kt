package com.clonect.feeltalk.presentation.util

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.clonect.feeltalk.R

fun TextView.addTextGradient() {
    val width = paint.measureText(text.toString())
    val textShader: Shader = LinearGradient(0f, 0f, 0f, textSize, intArrayOf(
        ContextCompat.getColor(context, R.color.gradient_start),
        ContextCompat.getColor(context, R.color.gradient_end)
    ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
    paint.shader = textShader
    setTextColor(ContextCompat.getColor(context, R.color.gradient_start))
}