package com.clonect.feeltalk.presentation.ui.input

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleAnniversaryInputBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CoupleAnniversaryInputFragment : Fragment() {

    private lateinit var binding: FragmentCoupleAnniversaryInputBinding
    private val viewModel: UserInputViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleAnniversaryInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectCoupleAnniversary()
        collectInvalidWarning()
        collectIsUserInfoUpdateSuccessful()

        enableNextButton(false)
        initCoupleAnniversaryEditText()

        binding.apply {
            textMessageHighlight.addTextGradient()
            btnBack.setOnClickListener {
                onBackCallback.handleOnBackPressed()
            }
            ivCoupleAnniversaryClear.setOnClickListener {
                metCoupleAnniversary.text?.clear()
            }
            mcvNext.setOnClickListener {
                updateUserInfo()
            }
        }
    }

    private fun updateUserInfo() {
        binding.mcvNext.isEnabled = false
        viewModel.updateUserInfo()
    }

    private fun navigateToCoupleRegistrationPage() {
        findNavController().navigate(R.id.action_coupleAnniversaryInputFragment_to_coupleRegistrationFragment)
    }

    private fun collectIsUserInfoUpdateSuccessful() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isUserInfoUpdateSuccessful.collectLatest {
                if (it) {
                    navigateToCoupleRegistrationPage()
                }
                binding.mcvNext.isEnabled = true
            }
        }
    }


    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.coupleAnniversary.collectLatest { anniversaryDate ->
                if (anniversaryDate.isNullOrBlank()) {
                    viewModel.setInvalidCoupleAnniversaryWarning(null)
                    binding.ivCoupleAnniversaryClear.visibility = View.GONE
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!binding.metCoupleAnniversary.isDone) {
                    viewModel.setInvalidCoupleAnniversaryWarning("yyyy/MM/dd")
                    binding.ivCoupleAnniversaryClear.visibility = View.VISIBLE
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!viewModel.checkValidDate(anniversaryDate)) {
                    viewModel.setInvalidCoupleAnniversaryWarning("존재하지 않는 날짜입니다.")
                    binding.ivCoupleAnniversaryClear.visibility = View.VISIBLE
                    enableNextButton(false)
                    return@collectLatest
                }

                viewModel.setInvalidCoupleAnniversaryWarning(null)
                binding.ivCoupleAnniversaryClear.visibility = View.VISIBLE
                enableNextButton(true)

            }
        }
    }

    private fun collectInvalidWarning() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.invalidCoupleAnniversaryWarning.collectLatest {
                binding.tvInvalidWarning.text = it
            }
        }
    }


    private fun initCoupleAnniversaryEditText() = binding.metCoupleAnniversary.apply {
        setText(viewModel.coupleAnniversary.value)
        addTextChangedListener {
            val input = it?.toString()
            viewModel.setCoupleAnniversary(input)
        }
        setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                && getNextButtonEnabled()
            ) {
                updateUserInfo()
                return@setOnEditorActionListener true
            }
            false
        }
        requestFocus()
    }

    private fun enableNextButton(enabled: Boolean) {
        binding.mcvNext.apply {
            val colorId =
                if (enabled) R.color.today_question_enter_answer_button_enabled_color
                else R.color.today_question_enter_answer_button_disabled_color

            setCardBackgroundColor(ResourcesCompat.getColor(resources, colorId, null))
            isClickable = enabled
            isFocusable = enabled
            isEnabled = enabled
        }
    }

    private fun getNextButtonEnabled() = binding.mcvNext.isEnabled


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}