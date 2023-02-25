package com.clonect.feeltalk.presentation.ui.input

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleAnniversaryInputBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import com.clonect.feeltalk.presentation.utils.makeLoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoupleAnniversaryInputFragment : Fragment() {

    private lateinit var binding: FragmentCoupleAnniversaryInputBinding
    private val viewModel: UserInputViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleAnniversaryInputBinding.inflate(inflater, container, false)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectCoupleAnniversary()
        collectInvalidWarning()
        collectIsUserInfoUpdateCompleted()
        collectIsLoading()

        enableNextButton(false)
        initCoupleAnniversaryEditText()

        binding.apply {
            textMessageHighlight.addTextGradient()
            btnBack.setOnClickListener {
                onBackCallback.handleOnBackPressed()
            }
            mcvNext.setOnClickListener {
                updateUserInfo()
            }
        }
    }

    private fun updateUserInfo() {
        viewModel.updateUserInfo()
    }

    private fun navigateToCoupleRegistrationPage() {
        findNavController().navigate(R.id.action_coupleAnniversaryInputFragment_to_guideFragment)
    }


    private fun collectIsLoading() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isLoading.collectLatest { isLoading ->
                if (isLoading) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun collectIsUserInfoUpdateCompleted() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isUserInfoUpdateCompleted.collectLatest { isCompleted ->
                if (isCompleted) {
                    navigateToCoupleRegistrationPage()
                    viewModel.clear()
                }
            }
        }
    }

    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.coupleAnniversary.collectLatest { anniversaryDate ->
                if (anniversaryDate.isNullOrBlank()) {
                    viewModel.setInvalidCoupleAnniversaryWarning(null)
                    binding.metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!binding.metCoupleAnniversary.isDone) {
                    viewModel.setInvalidCoupleAnniversaryWarning(getString(R.string.warning_invalid_date_format))
                    binding.metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!viewModel.checkValidDate(anniversaryDate)) {
                    viewModel.setInvalidCoupleAnniversaryWarning(getString(R.string.warning_no_such_date))
                    binding.metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                    enableNextButton(false)
                    return@collectLatest
                }

                viewModel.setInvalidCoupleAnniversaryWarning(null)
                binding.metCoupleAnniversary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
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


    @SuppressLint("ClickableViewAccessibility")
    private fun initCoupleAnniversaryEditText() = binding.metCoupleAnniversary.apply {
        setText(viewModel.coupleAnniversary.value)
        addTextChangedListener {
            val input = it?.toString()
            viewModel.setCoupleAnniversary(input)
        }
        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    binding.metCoupleAnniversary.text?.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
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
        loadingDialog.dismiss()
    }
}