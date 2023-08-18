package com.clonect.feeltalk.new_presentation.ui.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.DialogConfirmBinding

fun Fragment.makeLoadingDialog(onDismiss: () -> Unit = {}): Dialog {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_loading)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    dialog.setCancelable(false)
    dialog.setOnDismissListener { onDismiss() }

    return dialog
}

fun Fragment.showConfirmDialog(
    title: String,
    body: String?,
    cancelButton: String = requireContext().getString(R.string.dialog_cancel),
    confirmButton: String = requireContext().getString(R.string.dialog_confirm),
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit
) {
    val binding = DialogConfirmBinding.inflate(layoutInflater)
    val dialog = Dialog(requireContext()).apply {
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    if (dialog.isShowing) return

    binding.apply {
        if (body == null) {
            tvBody.visibility = View.GONE
        } else {
            tvBody.visibility = View.VISIBLE
            tvBody.text = body
        }
        tvTitle.text = title
        tvConfirm.text = confirmButton
        tvCancel.text = cancelButton

        mcvConfirm.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        mcvCancel.setOnClickListener {
            onCancel()
            dialog.dismiss()
        }
    }

    dialog.show()
}