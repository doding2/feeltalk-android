package com.clonect.feeltalk.presentation.ui.couple_registration

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleRegistrationBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import com.clonect.feeltalk.presentation.utils.makeLoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoupleRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentCoupleRegistrationBinding
    private val viewModel: CoupleRegistrationViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleRegistrationBinding.inflate(inflater, container, false)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectIsLoading()
        collectMyCoupleCode()
        initPartnerCoupleCodeValue()

        collectIsKeyPairExchangingCompleted()

        binding.apply {
            textMessageHighlight.addTextGradient()
            textMyCoupleCode.addTextGradient()
            btnBack.setOnClickListener {
                onBackCallback.handleOnBackPressed()
            }
            btnNext.setOnClickListener {
                sendPartnerCode()
            }
            cvCopyCode.setOnClickListener {
                copyCodeToClipBoard()
            }
        }

        initPartnerCodeEditText()
        collectToast()
        enableNextButton(false)
    }

    private fun sendPartnerCode() = lifecycleScope.launch {
        viewModel.sendPartnerCode()
    }


    private fun navigateCoupleRegistrationDonePage() {
        findNavController().navigate(R.id.action_coupleRegistrationFragment_to_coupleRegistrationDoneFragment)
    }

    private fun copyCodeToClipBoard() {
        val text = viewModel.myCoupleCode.value
        if (text.isBlank()) return

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("초대코드", text)
        clipboard.setPrimaryClip(clip)
    }


    private fun collectIsLoading() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isLoading.collectLatest {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun collectMyCoupleCode() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.myCoupleCode.collectLatest {
                binding.textMyCoupleCode.text = it
            }
        }
    }

    private fun collectIsKeyPairExchangingCompleted() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isKeyPairExchangingCompleted.collectLatest { isCompleted ->
                if (isCompleted) {
                    navigateCoupleRegistrationDonePage()
                }
            }
        }
    }

    private fun collectToast() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.toastMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initPartnerCoupleCodeValue() {
        binding.etPartnerCode.setText(viewModel.partnerCoupleCodeInput.value)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPartnerCodeEditText() = binding.etPartnerCode.apply {
        addTextChangedListener {
            val input = it?.toString() ?: ""
            viewModel.setPartnerCodeInput(input)

            val clearIcon = if (input.isBlank()) {
                enableNextButton(false)
                0
            } else {
                if (input == viewModel.myCoupleCode.value) {
                    enableNextButton(false)
                } else {
                    enableNextButton(true)
                }
                R.drawable.ic_clear
            }
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    binding.etPartnerCode.setText("")
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun enableNextButton(enabled: Boolean) = binding.btnNext.apply {
        val colorId =
            if (enabled) R.color.today_question_enter_answer_button_enabled_color
            else R.color.today_question_enter_answer_button_disabled_color

        setCardBackgroundColor(ResourcesCompat.getColor(resources, colorId, null))
        isClickable = enabled
        isFocusable = enabled
        isEnabled = enabled
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
        loadingDialog.dismiss()
    }

}