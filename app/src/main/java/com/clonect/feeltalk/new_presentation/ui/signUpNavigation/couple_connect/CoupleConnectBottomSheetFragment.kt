package com.clonect.feeltalk.new_presentation.ui.signUpNavigation.couple_connect

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleConnectBottomSheetBinding
import com.clonect.feeltalk.new_presentation.ui.signUpNavigation.SignUpNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.SoftKeyboardDetectorView
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoupleConnectBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "CoupleConnectBottomSheetFragment"
    }

    private lateinit var binding: FragmentCoupleConnectBottomSheetBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleConnectBottomSheetBinding.inflate(inflater, container, false)
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.skipCollapsed = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        setKeyboardListeners()

        binding.run {
            etPartnerCoupleCode.addTextChangedListener {
                viewModel.setPartnerCoupleCode(it?.toString() ?: "")
            }

            mcvConnect.setOnClickListener { matchCoupleCode() }
        }
    }

    private fun matchCoupleCode() {
        dialog?.dismiss()
        viewModel.matchCoupleCode()
    }


    private fun setKeyboardListeners() = binding.run {
        etPartnerCoupleCode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                etPartnerCoupleCode.clearFocus()
            }
            false
        }

        llClearFocusArea.setOnClickListener { hideKeyboard() }

        val keyboardListener = SoftKeyboardDetectorView(requireActivity())
        requireActivity().addContentView(keyboardListener, FrameLayout.LayoutParams(-1, -1))
        keyboardListener.setOnShownKeyboard {
            setConnectButtonMargin(0)
            mcvConnect.radius = 0f
        }
        keyboardListener.setOnHiddenKeyboard {
            etPartnerCoupleCode.clearFocus()
            setConnectButtonMargin(activity.dpToPx(20f).toInt())
            mcvConnect.radius = activity.dpToPx(30f)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etPartnerCoupleCode.windowToken, 0)
    }

    private fun setConnectButtonMargin(margin: Int) = binding.run {
        val params = mcvConnect.layoutParams as LinearLayout.LayoutParams
        params.setMargins(margin, 0, margin, 0)
        mcvConnect.layoutParams = params
    }

    private fun enableConnectButton(enabled: Boolean) = binding.mcvConnect.run {
        if (enabled) {
            setCardBackgroundColor(resources.getColor(R.color.main_500, null))
            isClickable = true
            isFocusable = true
            isEnabled = true
        } else {
            setCardBackgroundColor(resources.getColor(R.color.main_400, null))
            isClickable = false
            isFocusable = false
            isEnabled = false
        }
    }


    private fun validatePartnerCoupleCode(code: String?): Boolean {
        if (code.isNullOrBlank()) return false
        if (code == viewModel.coupleCode.value) return false
        return true
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.partnerCoupleCode.collectLatest {
                    enableConnectButton(validatePartnerCoupleCode(it))
                }
            }
        }
    }
}