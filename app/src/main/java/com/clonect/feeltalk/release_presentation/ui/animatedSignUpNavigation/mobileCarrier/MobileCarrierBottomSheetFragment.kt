package com.clonect.feeltalk.release_presentation.ui.animatedSignUpNavigation.mobileCarrier

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clonect.feeltalk.databinding.FragmentMobileCarrierBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MobileCarrierBottomSheetFragment(
    private val onSelected: (Int) -> Unit,
    private val onCancel: () -> Unit
): BottomSheetDialogFragment() {

    companion object {
        const val TAG = "MobileCarrierBottomSheetFragment"
    }

    private lateinit var binding: FragmentMobileCarrierBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMobileCarrierBottomSheetBinding.inflate(inflater, container, false)
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.skipCollapsed = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            mcvSelect1.setOnClickListener { select(1) }
            mcvSelect2.setOnClickListener { select(2) }
            mcvSelect3.setOnClickListener { select(3) }
            mcvSelect4.setOnClickListener { select(4) }
            mcvSelect5.setOnClickListener { select(5) }
            mcvSelect6.setOnClickListener { select(6) }
        }
    }

    private fun select(item: Int) {
        onSelected(item)
        dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancel()
    }
}