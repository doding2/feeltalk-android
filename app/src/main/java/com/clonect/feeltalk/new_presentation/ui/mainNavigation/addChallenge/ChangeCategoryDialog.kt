package com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.google.android.material.card.MaterialCardView

fun Fragment.showChangeCategoryDialog(
    previousCategory: ChallengeCategory,
    onConfirm: (ChallengeCategory) -> Unit
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_change_category)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    if (dialog.isShowing) return

    var selected = previousCategory
    selected = ChallengeCategory.eight

    val btnConfirm = dialog.findViewById<MaterialCardView>(R.id.mcv_change_category)
    btnConfirm.setOnClickListener {
        onConfirm(selected)
        dialog.dismiss()
    }

    dialog.show()
}