package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.inquire

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.clonect.feeltalk.databinding.FragmentInquireBinding
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/09/24.
 */
@AndroidEntryPoint
class InquireFragment : Fragment() {

    private lateinit var binding: FragmentInquireBinding
    private val viewModel: InquireViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInquireBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        loadingDialog = makeLoadingDialog()
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

            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }
            etEmail.addTextChangedListener { viewModel.setEmail(it?.toString()) }

            ivTitleClear.setOnClickListener { etTitle.setText("") }
            ivEmailClear.setOnClickListener { etEmail.setText("") }

            tvNext.setOnClickListener { navigateFocus() }
            mcvSubmit.setOnClickListener { submitInquiry() }
        }
    }


    private fun submitInquiry() {
        viewModel.submitInquiry {
            setFragmentResult(
                requestKey = "inquireFragment",
                result = bundleOf("inquirySucceed" to true)
            )
            findNavController().popBackStack()
        }
    }

    private fun navigateFocus() = binding.run {
        when (viewModel.focusedEditText.value) {
            "title" -> {
                etBody.requestFocus()
                showKeyboard(etBody)
            }
            "body" -> {
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
        etTitle.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("title")
        }
        etBody.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("body")
        }
        etEmail.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("email")
        }

        etTitle.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                etBody.requestFocus()
                etBody.performClick()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setEditTextNestedScroll() = binding.run {
        etBody.setOnTouchListener { view, motionEvent ->
            if (etBody.hasFocus()) {
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


    private fun changeNumBodyView(body: String?) = binding.run {
        tvNumBody.text = body?.length?.toString() ?: requireContext().getString(R.string.add_challenge_default_num_body)
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
            "title" -> {
                mcvTitle.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvTitle.setCardBackgroundColor(Color.WHITE)
                ivTitleClear.visibility = View.VISIBLE

                mcvBody.strokeWidth = 0
                mcvBody.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                mcvEmail.strokeWidth = 0
                mcvEmail.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivEmailClear.visibility = View.GONE

                tvNext.setText(R.string.add_challenge_next)
            }
            "body" -> {
                mcvTitle.strokeWidth = 0
                mcvTitle.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivTitleClear.visibility = View.GONE

                mcvBody.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvBody.setCardBackgroundColor(Color.WHITE)

                mcvEmail.strokeWidth = 0
                mcvEmail.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivEmailClear.visibility = View.GONE

                tvNext.setText(R.string.add_challenge_next)
            }
            "email" -> {
                mcvTitle.strokeWidth = 0
                mcvTitle.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivTitleClear.visibility = View.GONE

                mcvBody.strokeWidth = 0
                mcvBody.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                mcvEmail.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvEmail.setCardBackgroundColor(Color.WHITE)
                ivEmailClear.visibility = View.VISIBLE

                tvNext.setText(R.string.add_challenge_done)
            }
            else -> {
                mcvTitle.strokeWidth = 0
                mcvTitle.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivTitleClear.visibility = View.GONE

                mcvBody.strokeWidth = 0
                mcvBody.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                mcvEmail.strokeWidth = 0
                mcvEmail.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivEmailClear.visibility = View.GONE

                tvNext.setText(R.string.add_challenge_next)

                etTitle.clearFocus()
                etBody.clearFocus()
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
            launch { viewModel.body.collectLatest(::changeNumBodyView) }
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
                        title = requireContext().getString(R.string.inquire_dialog_title),
                        body = requireContext().getString(R.string.inquire_dialog_title),
                        cancelButton = requireContext().getString(R.string.inquire_dialog_cancel),
                        confirmButton = requireContext().getString(R.string.inquire_dialog_confirm),
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