package com.clonect.feeltalk.presentation.util

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R

fun Fragment.showAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_gradient_alert)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val orientation = resources.configuration.orientation

        val widthParam =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) ViewGroup.LayoutParams.MATCH_PARENT
            else ViewGroup.LayoutParams.WRAP_CONTENT

        window?.setLayout(widthParam, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    val titleText = dialog.findViewById<TextView>(R.id.text_title)
    titleText.text = title

    val messageText = dialog.findViewById<TextView>(R.id.text_message)
    messageText.text = message

    val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)
    btnConfirm.text = confirmButtonText
    btnConfirm.setOnClickListener {
        onConfirmClick()
        dialog.dismiss()
    }

    val btnCancel = dialog.findViewById<ImageView>(R.id.btn_cancel)
    btnCancel.setOnClickListener {
        onCancelClick()
        dialog.dismiss()
    }

    dialog.show()
}