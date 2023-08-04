package com.clonect.feeltalk.new_presentation.ui.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.clonect.feeltalk.databinding.SnackbarTextBinding
import com.google.android.material.snackbar.Snackbar

class TextSnackbar(view: View, private val message: String, duration: Int, private val bottomMargin: Int, private val onClick: (Snackbar) -> Unit = {}) {

    companion object {
        fun make(
            view: View,
            message: String,
            duration: Int,
            bottomMargin: Int = 0,
            onClick: (Snackbar) -> Unit,
        ) = TextSnackbar(view, message, duration, bottomMargin, onClick)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", duration)
    val view = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding = SnackbarTextBinding.inflate(inflater, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        view.apply {
            removeAllViews()
            setPadding(0, 0, 0, bottomMargin)
            setBackgroundColor(Color.TRANSPARENT)
            addView(binding.root, 0)
        }
    }

    private fun initData() {
        binding.apply {
            tvMessage.text = message
            root.setOnClickListener {
                onClick(snackbar)
            }
        }
    }

    fun show() {
        snackbar.show()
    }
}