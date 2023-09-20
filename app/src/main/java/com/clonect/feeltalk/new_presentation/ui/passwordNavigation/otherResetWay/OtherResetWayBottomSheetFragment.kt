package com.clonect.feeltalk.new_presentation.ui.passwordNavigation.otherResetWay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentOtherResetWayBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherResetWayBottomSheetFragment(
    private val onPartnerHelp: () -> Unit = {},
): BottomSheetDialogFragment() {

    companion object {
        const val TAG = "OtherResetWayBottomSheetFragment"
    }

    private lateinit var binding: FragmentOtherResetWayBottomSheetBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOtherResetWayBottomSheetBinding.inflate(inflater, container, false)
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.skipCollapsed = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            ivExit.setOnClickListener { dismiss() }
            mcvPartnerHelp.setOnClickListener {
                dismiss()
                onPartnerHelp()
            }
            mcvSendEmail.setOnClickListener {
                dismiss()
                sendEmailToFeeltalkFeedback()
            }
        }
    }

    private fun sendEmailToFeeltalkFeedback() {
        try {
            sendEmailWithGmail()
        } catch (e: Exception) {
            sendEmailWithOtherApp()
        }
    }

    private fun sendEmailWithGmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            setPackage("com.google.android.gm")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.PILLOWTALK_FEEDBACK))
            putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.pillowtalk_feedback_reset_password))
        }
        if (emailIntent.resolveActivity(requireActivity().packageManager) != null)
            startActivity(emailIntent)
        startActivity(emailIntent)
    }

    private fun sendEmailWithOtherApp() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.PILLOWTALK_FEEDBACK))
            putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.pillowtalk_feedback_reset_password))
        }
        startActivity(Intent.createChooser(intent, null))
    }
}