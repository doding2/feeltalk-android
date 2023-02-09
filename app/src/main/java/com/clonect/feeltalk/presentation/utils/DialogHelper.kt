package com.clonect.feeltalk.presentation.utils

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.domain.model.data.user.Emotion

fun Fragment.makeLoadingDialog(onDismiss: () -> Unit = {}): Dialog {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_loading)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    dialog.setCancelable(false)
    dialog.setOnDismissListener { onDismiss() }

    return dialog
}



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

    if (dialog.isShowing)
        return

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


fun Fragment.showMyEmotionChangerDialog(
    currentEmotion: Emotion,
    onClickItem: (Emotion) -> Unit
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_my_emotion_changer)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    val topLayout = dialog.findViewById<LinearLayout>(R.id.ll_top)
    val bottomLayout = dialog.findViewById<LinearLayout>(R.id.ll_bottom)
    topLayout.setOnClickListener { dialog.dismiss() }
    bottomLayout.setOnClickListener { dialog.dismiss() }

    val itemHappy = dialog.findViewById<CardView>(R.id.cv_happy)
    itemHappy.setOnClickListener {
        onClickItem(Emotion.Happy)
        dialog.dismiss()
    }
    if (currentEmotion is Emotion.Happy)
        itemHappy.visibility = View.GONE

    val itemPuzzling= dialog.findViewById<CardView>(R.id.cv_puzzling)
    itemPuzzling.setOnClickListener {
        onClickItem(Emotion.Puzzling)
        dialog.dismiss()
    }
    if (currentEmotion is Emotion.Puzzling)
        itemPuzzling.visibility = View.GONE

    val itemBad = dialog.findViewById<CardView>(R.id.cv_bad)
    itemBad.setOnClickListener {
        onClickItem(Emotion.Bad)
        dialog.dismiss()
    }
    if (currentEmotion is Emotion.Bad)
        itemBad.visibility = View.GONE

    val itemAngry = dialog.findViewById<CardView>(R.id.cv_angry)
    itemAngry.setOnClickListener {
        onClickItem(Emotion.Angry)
        dialog.dismiss()
    }
    if (currentEmotion is Emotion.Angry)
        itemAngry.visibility = View.GONE

    dialog.show()
}



fun Fragment.showPermissionRequestDialog(
    title: String = "권한 설정",
    message: String = "이 기능을 사용하기 위해서는 권한을 설정해주셔야 합니다.",
    confirmButtonText: String = "설정하러 가기"
) {
    showAlertDialog(
        title = title,
        message = message,
        confirmButtonText = confirmButtonText,
        onConfirmClick = {
            val intent = Intent().apply {
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                data = uri
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            }
            startActivity(intent)
        }
    )
}

