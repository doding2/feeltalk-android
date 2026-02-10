package com.clonect.feeltalk.release_presentation.ui.mainNavigation.myPage.suggestQuestion

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSuggestQuestionBinding
import com.clonect.feeltalk.release_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.release_presentation.ui.util.dpToPx
import com.clonect.feeltalk.release_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.release_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.release_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.release_presentation.ui.util.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/09/26.
 */
@AndroidEntryPoint
class SuggestQuestionFragment : Fragment() {

    private lateinit var binding: FragmentSuggestQuestionBinding
    private val viewModel: SuggestQuestionViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSuggestQuestionBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        loadingDialog = makeLoadingDialog()
        viewModel.navigatePage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEditTextFocusListener()
        setEditTextNestedScroll()
        setKeyboardInsets()
        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }

            etIdea.addTextChangedListener { viewModel.setIdea(it?.toString()) }
            etEmail.addTextChangedListener { viewModel.setEmail(it?.toString()) }

            ivEmailClear.setOnClickListener { etEmail.setText("") }

            tvNext.setOnClickListener { navigateFocus() }
            mcvSubmit.setOnClickListener { submitInquiry() }
        }
    }


    private fun submitInquiry() {
        viewModel.submitSuggestion {
            setFragmentResult(
                requestKey = "suggestQuestionFragment",
                result = bundleOf("suggestionSucceed" to true)
            )
            findNavController().popBackStack()
        }
    }

    private fun navigateFocus() = binding.run {
        when (viewModel.focusedEditText.value) {
            "idea" -> {
                etEmail.requestFocus()
                showKeyboard(etEmail)
            }
            "email" -> {
                hideKeyboard()
            }
            else -> {
                hideKeyboard()
            }
        }
    }


    private fun setEditTextFocusListener() = binding.run {
        etIdea.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("idea")
        }
        etEmail.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("email")
        }
    }

    private fun setEditTextNestedScroll() = binding.run {
        etIdea.setOnTouchListener { view, motionEvent ->
            if (etIdea.hasFocus()) {
                view?.parent?.requestDisallowInterceptTouchEvent(true)
                if (motionEvent.action and MotionEvent.ACTION_MASK
                    == MotionEvent.ACTION_SCROLL) {
                    view?.parent?.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setKeyboardInsets() = binding.run {
        svScroll.setOnClickListener { hideKeyboard() }

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.stableInsetBottom
            }

            val isKeyboardUp = imeHeight != 0
            viewModel.setKeyboardUp(isKeyboardUp)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }

            if (imeHeight == 0) {
                binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
                binding.svScroll.smoothScrollBy(0, getNavigationBarHeight())
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showKeyboard(target: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(target, 0)
    }


    private fun changeNumIdeaView(idea: String?) = binding.run {
        tvNumIdea.text = idea?.length?.toString() ?: requireContext().getString(R.string.add_challenge_default_num_body)
    }

    private fun enableSubmitButton(enabled: Boolean) = binding.run {
        mcvSubmit.isClickable = enabled
        mcvSubmit.isFocusable = enabled
        mcvSubmit.isEnabled = enabled

        if (enabled) {
            tvSubmit.setBackgroundResource(R.drawable.n_background_button_main)
        } else {
            tvSubmit.setBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }

    private fun changeKeyboardUpView(isUp: Boolean) = binding.run {
        if (isUp) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvSubmit.visibility = View.GONE
        } else {
            mcvNewsBar.visibility = View.GONE
            mcvSubmit.visibility = View.VISIBLE
            viewModel.setFocusedEditText(null)
        }
    }

    private fun changeFocusedEditTextView(focused: String?) = binding.run {
        when (focused) {
            "idea" -> {
                mcvIdea.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvIdea.setCardBackgroundColor(Color.WHITE)

                mcvEmail.strokeWidth = 0
                mcvEmail.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivEmailClear.visibility = View.GONE

                tvNext.setText(R.string.add_challenge_next)
            }
            "email" -> {
                mcvIdea.strokeWidth = 0
                mcvIdea.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                mcvEmail.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvEmail.setCardBackgroundColor(Color.WHITE)
                ivEmailClear.visibility = View.VISIBLE

                tvNext.setText(R.string.add_challenge_done)
            }
            else -> {
                mcvIdea.strokeWidth = 0
                mcvIdea.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                mcvEmail.strokeWidth = 0
                mcvEmail.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivEmailClear.visibility = View.GONE

                tvNext.setText(R.string.add_challenge_next)

                etIdea.clearFocus()
                etEmail.clearFocus()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.isSubmitEnabled.collectLatest(::enableSubmitButton) }
            launch { viewModel.idea.collectLatest(::changeNumIdeaView) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeKeyboardUpView) }
            launch { viewModel.focusedEditText.collectLatest(::changeFocusedEditTextView) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isEdited()) {
                    showConfirmDialog(
                        title = requireContext().getString(R.string.suggest_question_dialog_title),
                        body = requireContext().getString(R.string.suggest_question_dialog_body),
                        cancelButton = requireContext().getString(R.string.suggest_question_dialog_cancel),
                        confirmButton = requireContext().getString(R.string.suggest_question_dialog_confirm),
                        onConfirm = {
                            findNavController().popBackStack()
                        }
                    )
                    return
                }
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