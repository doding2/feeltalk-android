package com.clonect.feeltalk.release_presentation.ui.util

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import com.clonect.feeltalk.databinding.SnackbarPokeBinding
import com.google.android.material.snackbar.Snackbar

class PokeSnackbar(
    view: View,
    private val message: String,
    private val pokeText: String,
    duration: Int,
    private val bottomMargin: Int,
    private val onClick: (Snackbar) -> Unit = {},
    private val onPoke: (Snackbar) -> Unit = {}
) {

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", duration)
    val view = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding = SnackbarPokeBinding.inflate(inflater, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        view.apply {
            removeAllViews()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setPadding(0, 0, 0, bottomMargin)
            } else {
                setPadding(0, 0, 0, bottomMargin + getNavigationBarHeight())
            }
            setBackgroundColor(Color.TRANSPARENT)
            addView(binding.root, 0)
        }
        snackbar.view.setOnClickListener {
            onClick(snackbar)
        }
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
    }

    private fun initData() {
        binding.apply {
            tvMessage.text = message
            tvPoke.text = pokeText
            root.setOnClickListener {
                onClick(snackbar)
            }
            tvPoke.setOnClickListener {
                onPoke(snackbar)
            }
        }
    }

    fun show() {
        snackbar.show()
    }

    companion object {
        fun make(
            view: View,
            message: String,
            pokeText: String,
            duration: Int,
            bottomMargin: Int = 0,
            onClick: (Snackbar) -> Unit,
            onPoke: (Snackbar) -> Unit
        ) = PokeSnackbar(view, message, pokeText, duration, bottomMargin, onClick, onPoke)
    }
}