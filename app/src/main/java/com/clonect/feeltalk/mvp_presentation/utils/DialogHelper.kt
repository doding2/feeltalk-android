package com.clonect.feeltalk.mvp_presentation.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.mvp_domain.model.data.user.Emotion
import com.santalu.maskara.widget.MaskEditText
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun Context.showAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val dialog = Dialog(this).apply {
        setContentView(R.layout.dialog_gradient_alert)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val orientation = resources.configuration.orientation

        val widthParam =
            if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) android.view.ViewGroup.LayoutParams.MATCH_PARENT
            else android.view.ViewGroup.LayoutParams.WRAP_CONTENT

        window?.setLayout(widthParam, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    if (dialog.isShowing)
        return

    val titleText = dialog.findViewById<TextView>(com.clonect.feeltalk.R.id.text_title)
    titleText.text = title

    val messageText = dialog.findViewById<TextView>(com.clonect.feeltalk.R.id.text_message)
    messageText.text = message

    val btnConfirm = dialog.findViewById<TextView>(com.clonect.feeltalk.R.id.btn_confirm)
    btnConfirm.text = confirmButtonText
    btnConfirm.setOnClickListener {
        onConfirmClick()
        dialog.dismiss()
    }

    val btnCancel = dialog.findViewById<ImageView>(com.clonect.feeltalk.R.id.btn_cancel)
    btnCancel.setOnClickListener {
        onCancelClick()
        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        onDismiss()
    }

    dialog.show()
}

fun Fragment.showAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
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

    dialog.setOnDismissListener {
        onDismiss()
    }

    dialog.show()
}


fun Fragment.showMyEmotionChangerDialog(
    currentEmotion: Emotion,
    onClickItem: (Emotion) -> Unit,
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


fun Fragment.showBreakUpCoupleDialog(
    partnerNickname: String,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_break_up_couple)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.BOTTOM)

        findViewById<LinearLayout>(R.id.ll_root).setOnClickListener {
            dismiss()
        }

        findViewById<TextView>(R.id.tv_partner_nickname).text = partnerNickname

        findViewById<TextView>(R.id.tv_divorce).setOnClickListener {
            onConfirm()
            dismiss()
        }

        findViewById<CardView>(R.id.cv_cancel).setOnClickListener {
            onCancel()
            dismiss()
        }

        setOnDismissListener { onDismiss() }
    }

    dialog.show()
}


fun Context.showPermissionRequestDialog(
    title: String = "권한 설정",
    message: String = "이 기능을 사용하기 위해서는 권한을 설정해주셔야 합니다.",
    confirmButtonText: String = "설정하러 가기",
) {
    showAlertDialog(
        title = title,
        message = message,
        confirmButtonText = confirmButtonText,
        onConfirmClick = {
            val intent = Intent().apply {
                val uri = Uri.fromParts("package", this@showPermissionRequestDialog.packageName, null)
                data = uri
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            }
            startActivity(intent)
        }
    )
}

fun Fragment.showPermissionRequestDialog(
    title: String = "권한 설정",
    message: String = "이 기능을 사용하기 위해서는 권한을 설정해주셔야 합니다.",
    confirmButtonText: String = "설정하러 가기",
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


@SuppressLint("ClickableViewAccessibility")
fun Fragment.showEditNicknameDialog(
    onDismiss: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onConfirmClick: (String, Dialog) -> Unit = { _, _ -> },
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_edit_nickname)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val orientation = resources.configuration.orientation

        val widthParam =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) ViewGroup.LayoutParams.MATCH_PARENT
            else ViewGroup.LayoutParams.WRAP_CONTENT

        window?.setLayout(widthParam, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    if (dialog.isShowing) return

    val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)
    val etNickname = dialog.findViewById<EditText>(R.id.et_nickname)
    val tvInvalidWarning = dialog.findViewById<TextView>(R.id.tv_invalid_warning)
    val btnCancel = dialog.findViewById<ImageView>(R.id.btn_cancel)

    fun enableConfirmButton(enabled: Boolean) {
        btnConfirm.isEnabled = enabled
        if (enabled) {
            btnConfirm.setBackgroundResource(R.drawable.background_dialog_gradient_button)
        } else {
            btnConfirm.setBackgroundResource(R.drawable.background_dialog_diabled_gradient_button)
        }
    }
    enableConfirmButton(false)


    val nicknamePattern = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9\\s]*$")


    etNickname.requestFocus()
    dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

    etNickname.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (etNickname.right - etNickname.compoundPaddingRight)) {
                etNickname.text.clear()
                return@setOnTouchListener true
            }
        }
        return@setOnTouchListener false
    }

    etNickname.addTextChangedListener { nickname ->
        if (nickname.isNullOrBlank()) {
            tvInvalidWarning.text = null
            etNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        if (nickname.length >= 20) {
            tvInvalidWarning.text = getString(R.string.warning_long_nickname)
            etNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        val isValidNickname = nicknamePattern.matcher(nickname).matches()
        if (!isValidNickname) {
            tvInvalidWarning.text = getString(R.string.warning_special_character_nickname)
            etNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        tvInvalidWarning.text = null
        etNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
        enableConfirmButton(true)
    }


    btnConfirm.setOnClickListener {
        val nickname = etNickname.text.toString()
        onConfirmClick(nickname, dialog)
    }

    btnCancel.setOnClickListener {
        onCancelClick()
        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        onDismiss()
    }

    dialog.show()
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.showEditBirthDialog(
    onDismiss: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onConfirmClick: (String, Dialog) -> Unit = { _, _ -> },
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_edit_birth)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val orientation = resources.configuration.orientation

        val widthParam =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) ViewGroup.LayoutParams.MATCH_PARENT
            else ViewGroup.LayoutParams.WRAP_CONTENT

        window?.setLayout(widthParam, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    if (dialog.isShowing) return

    val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)
    val metBirth = dialog.findViewById<MaskEditText>(R.id.met_birth)
    val tvInvalidWarning = dialog.findViewById<TextView>(R.id.tv_invalid_warning)
    val btnCancel = dialog.findViewById<ImageView>(R.id.btn_cancel)

    fun checkValidDate(date: String): Boolean {
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        format.isLenient = false

        return try {
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun enableConfirmButton(enabled: Boolean) {
        btnConfirm.isEnabled = enabled
        if (enabled) {
            btnConfirm.setBackgroundResource(R.drawable.background_dialog_gradient_button)
        } else {
            btnConfirm.setBackgroundResource(R.drawable.background_dialog_diabled_gradient_button)
        }
    }
    enableConfirmButton(false)



    metBirth.requestFocus()
    dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

    metBirth.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (metBirth.right - metBirth.compoundPaddingRight)) {
                metBirth.text?.clear()
                return@setOnTouchListener true
            }
        }
        return@setOnTouchListener false
    }

    metBirth.addTextChangedListener { date ->
        if (date.isNullOrBlank()) {
            tvInvalidWarning.text = null
            metBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        if (!metBirth.isDone) {
            tvInvalidWarning.text = getString(R.string.warning_invalid_date_format)
            metBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        if (!checkValidDate(date.toString())) {
            tvInvalidWarning.text = getString(R.string.warning_no_such_date)
            metBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        tvInvalidWarning.text = null
        metBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
        enableConfirmButton(true)
    }


    btnConfirm.setOnClickListener {
        val date = metBirth.masked
        onConfirmClick(date, dialog)
    }

    btnCancel.setOnClickListener {
        onCancelClick()
        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        onDismiss()
    }

    dialog.show()
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.showEditCoupleAnniversaryDialog(
    onDismiss: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onConfirmClick: (String, Dialog) -> Unit = { _, _ -> },
) {
    val dialog = Dialog(requireContext()).apply {
        setContentView(R.layout.dialog_edit_couple_anniversary)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val orientation = resources.configuration.orientation

        val widthParam =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) ViewGroup.LayoutParams.MATCH_PARENT
            else ViewGroup.LayoutParams.WRAP_CONTENT

        window?.setLayout(widthParam, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    if (dialog.isShowing) return

    val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)
    val metCoupleAnniversary = dialog.findViewById<MaskEditText>(R.id.met_couple_anniversary)
    val tvInvalidWarning = dialog.findViewById<TextView>(R.id.tv_invalid_warning)
    val btnCancel = dialog.findViewById<ImageView>(R.id.btn_cancel)

    fun checkValidDate(date: String): Boolean {
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        format.isLenient = false

        return try {
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun enableConfirmButton(enabled: Boolean) {
        btnConfirm.isEnabled = enabled
        if (enabled) {
            btnConfirm.setBackgroundResource(R.drawable.background_dialog_gradient_button)
        } else {
            btnConfirm.setBackgroundResource(R.drawable.background_dialog_diabled_gradient_button)
        }
    }
    enableConfirmButton(false)



    metCoupleAnniversary.requestFocus()
    dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

    metCoupleAnniversary.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (metCoupleAnniversary.right - metCoupleAnniversary.compoundPaddingRight)) {
                metCoupleAnniversary.text?.clear()
                return@setOnTouchListener true
            }
        }
        return@setOnTouchListener false
    }

    metCoupleAnniversary.addTextChangedListener { date ->
        if (date.isNullOrBlank()) {
            tvInvalidWarning.text = null
            metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        if (!metCoupleAnniversary.isDone) {
            tvInvalidWarning.text = getString(R.string.warning_invalid_date_format)
            metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        if (!checkValidDate(date.toString())) {
            tvInvalidWarning.text = getString(R.string.warning_no_such_date)
            metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
            enableConfirmButton(false)
            return@addTextChangedListener
        }

        tvInvalidWarning.text = null
        metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
        enableConfirmButton(true)
    }


    btnConfirm.setOnClickListener {
        val date = metCoupleAnniversary.masked
        onConfirmClick(date, dialog)
    }

    btnCancel.setOnClickListener {
        onCancelClick()
        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        onDismiss()
    }

    dialog.show()
}