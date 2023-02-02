package com.clonect.feeltalk.presentation.ui.couple_registration

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.LayoutInflater
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoupleRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentCoupleRegistrationBinding
    private val viewModel: CoupleRegistrationViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectMyCoupleCode()
        initPartnerCoupleCodeValue()

        binding.apply {
            textMessageHighlight.addTextGradient()
            textMyCoupleCode.addTextGradient()
            btnBack.setOnClickListener {
                onBackCallback.handleOnBackPressed()
            }
            btnNext.setOnClickListener {
                sendPartnerCode()
            }
            ivPartnerCodeClear.setOnClickListener {
                etPartnerCode.setText("")
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
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            val isSuccessful = viewModel.sendPartnerCode()
            if (isSuccessful) {
                navigateHomePage()
                return@repeatOnLifecycle
            }

            Toast.makeText(requireContext(), "올바르지 않은 코드입니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateHomePage() {
        findNavController().navigate(R.id.action_coupleRegistrationFragment_to_bottomNavigationFragment)
    }

    private fun copyCodeToClipBoard() {
        val text = viewModel.myCoupleCode.value
        if (text.isBlank()) return

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("초대코드", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun collectMyCoupleCode() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.myCoupleCode.collectLatest {
                binding.textMyCoupleCode.text = it
            }
        }
    }

    private fun collectToast() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.toastMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initPartnerCoupleCodeValue() {
        binding.etPartnerCode.setText(viewModel.partnerCoupleCodeInput.value)
    }

    private fun initPartnerCodeEditText() {
        binding.etPartnerCode.addTextChangedListener {
            val input = it?.toString() ?: ""
            viewModel.setPartnerCodeInput(input)

            if (input.isBlank()) {
                binding.ivPartnerCodeClear.visibility = View.GONE
                enableNextButton(false)
            } else {
                binding.ivPartnerCodeClear.visibility = View.VISIBLE

                if (input == viewModel.myCoupleCode.value) {
                    enableNextButton(false)
                } else {
                    enableNextButton(true)
                }
            }
        }
    }

    private fun enableNextButton(enabled: Boolean) {
        binding.btnNext.apply {
            val colorId =
                if (enabled) R.color.today_question_enter_answer_button_enabled_color
                else R.color.today_question_enter_answer_button_disabled_color

            setCardBackgroundColor(ResourcesCompat.getColor(resources, colorId, null))
            isClickable = enabled
            isFocusable = enabled
        }
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
    }
}