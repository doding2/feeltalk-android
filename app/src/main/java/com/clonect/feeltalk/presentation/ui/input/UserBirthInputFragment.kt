package com.clonect.feeltalk.presentation.ui.input

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentUserBirthInputBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserBirthInputFragment : Fragment() {

    private lateinit var binding: FragmentUserBirthInputBinding
    private val viewModel: UserInputViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserBirthInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectBirth()
        collectInvalidWarning()

        enableNextButton(false)
        initBirthEditText()

        binding.apply {
            textMessageHighlight.addTextGradient()
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivUserBirthClear.setOnClickListener { metUserBirth.text?.clear() }
            mcvNext.setOnClickListener { navigateToCoupleAnniversaryPage() }
        }
    }


    private fun navigateToCoupleAnniversaryPage() {
        // TODO
        Toast.makeText(requireContext(), "성공", Toast.LENGTH_SHORT).show()
    }


    private fun collectBirth() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.birth.collectLatest { birthDate ->
                if (birthDate.isNullOrBlank()) {
                    viewModel.setInvalidBirthWarning(null)
                    binding.ivUserBirthClear.visibility = View.GONE
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!binding.metUserBirth.isDone) {
                    viewModel.setInvalidBirthWarning("yyyy/MM/dd")
                    binding.ivUserBirthClear.visibility = View.VISIBLE
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!viewModel.checkValidDate(birthDate)) {
                    viewModel.setInvalidBirthWarning("존재하지 않는 날짜입니다.")
                    binding.ivUserBirthClear.visibility = View.VISIBLE
                    enableNextButton(false)
                    return@collectLatest
                }

                viewModel.setInvalidBirthWarning(null)
                binding.ivUserBirthClear.visibility = View.VISIBLE
                enableNextButton(true)

            }
        }
    }

    private fun collectInvalidWarning() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.invalidBirthWarning.collectLatest {
                binding.tvInvalidWarning.text = it
            }
        }
    }


    private fun initBirthEditText() = binding.metUserBirth.apply {
        setText(viewModel.birth.value)
        addTextChangedListener {
            val input = it?.toString()
            viewModel.setBirth(input)
        }
        setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                && getNextButtonEnabled()
            ) {
                navigateToCoupleAnniversaryPage()
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