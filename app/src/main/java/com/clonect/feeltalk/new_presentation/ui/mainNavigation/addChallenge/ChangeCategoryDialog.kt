package com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
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

    fun highlight(select: MaterialCardView) {
        select.strokeWidth = requireContext().dpToPx(2f).toInt()
    }
    fun cancel(select: MaterialCardView) {
        select.strokeWidth = 0
    }

    var selected = previousCategory
    var selectedView: MaterialCardView

    val select1 = dialog.findViewById<MaterialCardView>(R.id.mcv_select_1)
    val select2 = dialog.findViewById<MaterialCardView>(R.id.mcv_select_2)
    val select3 = dialog.findViewById<MaterialCardView>(R.id.mcv_select_3)
    val select4 = dialog.findViewById<MaterialCardView>(R.id.mcv_select_4)
    val select5 = dialog.findViewById<MaterialCardView>(R.id.mcv_select_5)
    val select6 = dialog.findViewById<MaterialCardView>(R.id.mcv_select_6)

    selectedView = when (selected) {
        ChallengeCategory.Place -> select1
        ChallengeCategory.Toy -> select2
        ChallengeCategory.Pose -> select3
        ChallengeCategory.Clothes -> select4
        ChallengeCategory.Whip -> select5
        ChallengeCategory.Handcuffs -> select6
        ChallengeCategory.VideoCall -> select1
        ChallengeCategory.Porn -> select1
    }
    highlight(selectedView)


    select1.setOnClickListener {
        selected = ChallengeCategory.Place
        cancel(selectedView)
        selectedView = select1
        highlight(select1)
    }
    select2.setOnClickListener {
        selected = ChallengeCategory.Toy
        cancel(selectedView)
        selectedView = select2
        highlight(select2)
    }
    select3.setOnClickListener {
        selected = ChallengeCategory.Pose
        cancel(selectedView)
        selectedView = select3
        highlight(select3)
    }
    select4.setOnClickListener {
        selected = ChallengeCategory.Clothes
        cancel(selectedView)
        selectedView = select4
        highlight(select4)
    }
    select5.setOnClickListener {
        selected = ChallengeCategory.Whip
        cancel(selectedView)
        selectedView = select5
        highlight(select5)
    }
    select6.setOnClickListener {
        selected = ChallengeCategory.Handcuffs
        cancel(selectedView)
        selectedView = select6
        highlight(select6)
    }


    val btnConfirm = dialog.findViewById<MaterialCardView>(R.id.mcv_change_category)
    btnConfirm.setOnClickListener {
        onConfirm(selected)
        dialog.dismiss()
    }

    dialog.show()
}