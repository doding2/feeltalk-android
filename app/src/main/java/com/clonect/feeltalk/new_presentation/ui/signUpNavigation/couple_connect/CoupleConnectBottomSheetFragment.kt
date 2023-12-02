package com.clonect.feeltalk.new_presentation.ui.signUpNavigation.couple_connect

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleConnectBottomSheetBinding
import com.clonect.feeltalk.new_presentation.ui.signUpNavigation.SignUpNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoupleConnectBottomSheetFragment(
    private val onKeyboardUp: () -> Unit = {},
    private val onKeyboardDown: () -> Unit = {}
) : BottomSheetDialogFragment() {

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
        setKeyboardInsets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            etPartnerCoupleCode.addTextChangedListener {
                viewModel.setPartnerCoupleCode(it?.toString() ?: "")
            }

            mcvConnect.setOnClickListener { matchCoupleCode() }
        }
    }

    private fun matchCoupleCode() {
        val decorView = activity?.window?.decorView
        decorView?.setOnApplyWindowInsetsListener(null)
        dialog?.dismiss()
        viewModel.matchCoupleCode()
    }


    private fun setKeyboardInsets() = binding.run {
        etPartnerCoupleCode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                etPartnerCoupleCode.clearFocus()
            }
            false
        }

        llClearFocusArea.setOnClickListener { hideKeyboard() }

        val decorView = activity?.window?.decorView
            ?: return
        decorView.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.stableInsetBottom
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                setConnectButtonMargin(0)
                mcvConnect.radius = 0f
                onKeyboardUp()
                return@setOnApplyWindowInsetsListener insets
            }

            val isKeyboardUp = imeHeight != 0
            if (isKeyboardUp) {
                setConnectButtonMargin(0)
                mcvConnect.radius = 0f
                onKeyboardUp()
            } else {
                etPartnerCoupleCode.clearFocus()
                setConnectButtonMargin(activity.dpToPx(20f).toInt())
                mcvConnect.radius = activity.dpToPx(30f)
                onKeyboardDown()
            }

            insets
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
            binding.tvConnect.setBackgroundResource(R.drawable.n_background_button_main)
            isClickable = true
            isFocusable = true
            isEnabled = true
        } else {
            binding.tvConnect.setBackgroundColor(resources.getColor(R.color.main_400, null))
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