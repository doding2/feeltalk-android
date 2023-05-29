package com.clonect.feeltalk.new_presentation.ui.main_navigation.answer

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import com.google.android.material.card.MaterialCardView

fun Fragment.showAnswerConfirmDialog(
    onConfirm: () -> Unit
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_answer_confirm)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    if (dialog.isShowing) return

    val btnConfirm = dialog.findViewById<MaterialCardView>(R.id.mcv_confirm)
    btnConfirm.setOnClickListener {
        onConfirm()
        dialog.dismiss()
    }

    val btnCancel = dialog.findViewById<MaterialCardView>(R.id.mcv_cancel)
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}