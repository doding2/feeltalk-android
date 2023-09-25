package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.deleteAccountDetail

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
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentDeleteAccountDetailBinding
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
 * Created by doding2 on 2023/09/23.
 */
@AndroidEntryPoint
class DeleteAccountDetailFragment : Fragment() {

    private lateinit var binding: FragmentDeleteAccountDetailBinding
    private val viewModel: DeleteAccountDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDeleteAccountDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setKeyboardInsets()
        setEditTextNestedScroll()
        setEditTextFocusListener()
        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }

            etEtcReason.addTextChangedListener { viewModel.setEtcReason(it?.toString() ?: "") }
            etDeleteReason.addTextChangedListener { viewModel.setDeleteReason(it?.toString() ?: "") }

            mcvReason1.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.BreakUp) }
            mcvReason2.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.NoFunctionality) }
            mcvReason3.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.BugOrError) }
            mcvReason4.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.Etc) }

            tvNext.setOnClickListener { navigateFocus() }
            mcvConfirm.setOnClickListener { showDeleteAccountConfirmDialog() }
        }
    }

    private fun showDeleteAccountConfirmDialog() {
        showConfirmDialog(
            title = requireContext().getString(R.string.delete_account_detail_dialog_title),
            body = requireContext().getString(R.string.delete_account_detail_dialog_body),
            cancelButton = requireContext().getString(R.string.delete_account_detail_dialog_cancel),
            confirmButton = requireContext().getString(R.string.delete_account_detail_dialog_confirm),
            onConfirm = {
                deleteAccount()
            }
        )
    }

    private fun deleteAccount() {
        viewModel.deleteAccount {
            navigateToDeleteAccountDone()
        }
    }

    private fun navigateToDeleteAccountDone() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_deleteAccountDetailFragment_to_deleteAccountDoneFragment)
    }

    private fun navigateFocus() = binding.run {
        when (viewModel.focusedEditText.value) {
            "etc" -> {
                etDeleteReason.requestFocus()
                showKeyboard(etDeleteReason)
            }
            "delete" -> {
                hideKeyboard()
            }
            else -> {
                hideKeyboard()
            }
        }
    }


    private fun setKeyboardInsets() = binding.run {
        root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.systemWindowInsetBottom
            }

            viewModel.setKeyboardUp(imeHeight != 0)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }
            if (imeHeight == 0) {
                root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
                binding.svScroll.setPadding(0, 0, 0, 0)
            } else {
                val newsBarHeight = requireContext().dpToPx(55f).toInt()

                root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
                binding.svScroll.smoothScrollBy(0, getNavigationBarHeight() + newsBarHeight)
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

    private fun setEditTextFocusListener() = binding.run {
        etEtcReason.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("etc")
        }
        etDeleteReason.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) viewModel.setFocusedEditText("delete")
        }

        etEtcReason.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                etDeleteReason.requestFocus()
                etDeleteReason.performClick()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setEditTextNestedScroll() = binding.run {
        etEtcReason.setOnTouchListener { view, motionEvent ->
            if (etEtcReason.hasFocus()) {
                view?.parent?.requestDisallowInterceptTouchEvent(true)
                if (motionEvent.action and MotionEvent.ACTION_MASK
                    == MotionEvent.ACTION_SCROLL) {
                    view?.parent?.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
            false
        }
        etDeleteReason.setOnTouchListener { view, motionEvent ->
            if (etDeleteReason.hasFocus()) {
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


    private fun changeKeyboardUpView(isUp: Boolean) = binding.run {
        if (isUp) {
            llConfirm.visibility = View.GONE
            mcvNewsBar.visibility = View.VISIBLE
        } else {
            llConfirm.visibility = View.VISIBLE
            mcvNewsBar.visibility = View.GONE
            etEtcReason.clearFocus()
            etDeleteReason.clearFocus()
            viewModel.setFocusedEditText(null)
        }
    }

    private fun changeDeleteReasonView(deleteReasonType: DeleteReasonType?) = binding.run {
        ivReason1Check.setImageResource(R.drawable.n_ic_round_disagree)
        ivReason2Check.setImageResource(R.drawable.n_ic_round_disagree)
        ivReason3Check.setImageResource(R.drawable.n_ic_round_disagree)
        ivReason4Check.setImageResource(R.drawable.n_ic_round_disagree)
        mcvEtcReason.visibility = View.GONE

        when (deleteReasonType) {
            DeleteReasonType.BreakUp ->
                ivReason1Check.setImageResource(R.drawable.n_ic_round_agree)
            DeleteReasonType.NoFunctionality ->
                ivReason2Check.setImageResource(R.drawable.n_ic_round_agree)
            DeleteReasonType.BugOrError ->
                ivReason3Check.setImageResource(R.drawable.n_ic_round_agree)
            DeleteReasonType.Etc -> {
                ivReason4Check.setImageResource(R.drawable.n_ic_round_agree)
                mcvEtcReason.visibility = View.VISIBLE
            }
            null -> return@run
        }
    }

    private fun changeFocusedEditTextView(focused: String?) = binding.run {
        mcvEtcReason.strokeWidth = 0
        mcvEtcReason.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

        mcvDeleteReason.strokeWidth = 0
        mcvDeleteReason.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

        when (focused) {
            "etc" -> {
                mcvEtcReason.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvEtcReason.setCardBackgroundColor(Color.WHITE)
                tvNext.setText(R.string.add_challenge_next)
            }
            "delete" -> {
                mcvDeleteReason.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvDeleteReason.setCardBackgroundColor(Color.WHITE)
                tvNext.setText(R.string.add_challenge_done)
            }
            else -> {
                tvNext.setText(R.string.add_challenge_next)
                etEtcReason.clearFocus()
                etDeleteReason.clearFocus()
            }
        }
    }


    private fun changeConfirmEnabledView(enabled: Boolean) = binding.run {
        mcvConfirm.isEnabled = enabled
        if (enabled) {
            mcvConfirm.strokeWidth = requireContext().dpToPx(1f).toInt()
            mcvConfirm.setCardBackgroundColor(Color.WHITE)
            tvConfirm.setTextColor(Color.BLACK)
        } else {
            mcvConfirm.strokeWidth = 0
            mcvConfirm.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))
            tvConfirm.setTextColor(Color.WHITE)
        }
    }

    private fun changeEtcReasonNumView(reason: String) = binding.run {
        tvNumEtcReason.text = reason.length.toString()
    }

    private fun changeDeleteReasonNumView(reason: String) = binding.run {
        tvNumDeleteReason.text = reason.length.toString()
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
            launch { viewModel.isKeyboardUp.collectLatest(::changeKeyboardUpView) }
            launch { viewModel.deleteReasonType.collectLatest(::changeDeleteReasonView) }
            launch { viewModel.focusedEditText.collectLatest(::changeFocusedEditTextView) }
            launch { viewModel.isConfirmEnabled.collectLatest(::changeConfirmEnabledView) }
            launch { viewModel.etcReason.collectLatest(::changeEtcReasonNumView) }
            launch { viewModel.deleteReason.collectLatest(::changeDeleteReasonNumView) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
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