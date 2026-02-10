package com.clonect.feeltalk.mvp_presentation.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.clonect.feeltalk.R
import kotlin.math.cos
import kotlin.math.sin

fun TextView.addTextGradient() {
    val width = paint.measureText(text.toString())
    val angle = 45f
    val textShader: Shader = LinearGradient(
        0f,
        0f,
        sin(Math.PI * angle / 180).toFloat() * width,
        cos(Math.PI * angle / 180).toFloat() * width,
        intArrayOf(
            ContextCompat.getColor(context, R.color.main_gradient_start),
            ContextCompat.getColor(context, R.color.main_gradient_end)
        ),
        floatArrayOf(0f, 1f),
        Shader.TileMode.CLAMP
    )
    paint.shader = textShader
    setTextColor(ContextCompat.getColor(context, R.color.main_gradient_start))
}