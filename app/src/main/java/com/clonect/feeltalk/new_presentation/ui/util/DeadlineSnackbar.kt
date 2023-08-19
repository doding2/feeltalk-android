package com.clonect.feeltalk.new_presentation.ui.util

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import com.clonect.feeltalk.databinding.SnackbarDeadlineImminentBinding
import com.google.android.material.snackbar.Snackbar

class DeadlineSnackbar(view: View, duration: Int, private val bottomMargin: Int, private val onClick: (Snackbar) -> Unit = {}) {

    companion object {
        fun make(
            view: View,
            duration: Int,
            bottomMargin: Int = 0,
            onClick: (Snackbar) -> Unit,
        ) = DeadlineSnackbar(view, duration, bottomMargin, onClick)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", duration)
    val view = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding = SnackbarDeadlineImminentBinding.inflate(inflater, null, false)

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
//            updateLayoutParams<CoordinatorLayout.LayoutParams> {
//                gravity = Gravity.TOP
//            }
            setBackgroundColor(Color.TRANSPARENT)
            addView(binding.root, 0)
        }
        snackbar.view.setOnClickListener {
            onClick(snackbar)
        }
    }

    private fun initData() {
        binding.apply {
            root.setOnClickListener {
                onClick(snackbar)
            }
        }
    }

    fun setAnchorView(anchorView: View?) {
        snackbar.anchorView = anchorView
    }

    fun show() {
        snackbar.show()
    }
}