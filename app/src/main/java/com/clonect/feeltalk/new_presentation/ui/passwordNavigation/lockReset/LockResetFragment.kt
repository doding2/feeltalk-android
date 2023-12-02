package com.clonect.feeltalk.new_presentation.ui.passwordNavigation.lockReset

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentLockResetBinding
import com.clonect.feeltalk.new_presentation.ui.passwordNavigation.otherResetWay.OtherResetWayBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by doding2 on 2023/09/19.
 */
@AndroidEntryPoint
class LockResetFragment : Fragment() {

    private lateinit var binding: FragmentLockResetBinding
    private val viewModel: LockResetViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLockResetBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        setDatePickerListener()
        setKeyboardInsets()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            etAnswer.addTextChangedListener { viewModel.setLockAnswer(it?.toString()) }
            etAnswer.setOnFocusChangeListener { view, isFocused ->
                if (isFocused) {
                    viewModel.setLockAnswerFocused(true)
                }
            }
            ivClear.setOnClickListener { etAnswer.setText("") }
            mcvAnswerDate.setOnClickListener {
                viewModel.setShowInvalidAnswer(false)
                enableAnswerDatePicker(true)
            }

            clForgetAgain.setOnClickListener { showOtherResetWayBottomSheet() }

            tvNext.setOnClickListener {
                if (binding.dpAnswerDatePicker.isVisible) {
                    enableAnswerDatePicker(false)
                    hideConfirmButton(false)
                } else {
                    hideKeyboard()
                }
            }
            mcvConfirm.setOnClickListener { matchQuestionAnswer() }
        }
    }

    private fun showOtherResetWayBottomSheet() {
        hideKeyboard()
        val bottomSheet = OtherResetWayBottomSheetFragment(
            onPartnerHelp = {
                navigateToPartnerHelpReset()
            }
        )
        bottomSheet.show(requireActivity().supportFragmentManager, OtherResetWayBottomSheetFragment.TAG)
    }

    private fun navigateToPartnerHelpReset() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_lockResetFragment_to_partnerHelpResetFragment)
    }

    private fun matchQuestionAnswer() = lifecycleScope.launch {
        val isValid = viewModel.matchQuestionAnswer()
        if (!isValid) return@launch

        navigateToPasswordSettingDeepLink()
    }

    private fun navigateToPasswordSettingDeepLink() {
        requireParentFragment()
            .findNavController()
            .apply {
                navigate(R.id.mainNavigationFragment, bundleOf("isLockReset" to true))
                navigate(R.id.settingFragment)
                navigate(R.id.lockSettingFragment)
                navigate(R.id.passwordSettingFragment, bundleOf("isLockEnabled" to true))
            }
    }

    private fun setDatePickerListener() {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val now = viewModel.lockAnswerDate.value
        val dateString = formatter.format(now)
        val dateTokens = dateString.split("/")

        binding.dpAnswerDatePicker.init(
            dateTokens[0].toInt(),
            dateTokens[1].toInt() - 1,
            dateTokens[2].toInt()
        ) { picker, year, monthOfYear, dayOfMonth ->
            val str = "$year/${monthOfYear + 1}/$dayOfMonth"
            val date = formatter.parse(str)
            if (date != null) {
                viewModel.setLockAnswerDate(date)
            }
        }
    }

    private fun enableAnswerDatePicker(enabled: Boolean) = binding.run {
        if (enabled) {
            lifecycleScope.launch {
                if (viewModel.isKeyboardUp.value) {
                    hideKeyboard()
                    delay(100)
                }
                mcvConfirm.visibility = View.GONE
                mcvNewsBar.visibility = View.VISIBLE
                dpAnswerDatePicker.visibility = View.VISIBLE
                mcvAnswerDate.strokeWidth = activity.dpToPx(2f).toInt()
                mcvAnswerDate.setCardBackgroundColor(Color.WHITE)

                hideConfirmButton(true)
            }
        } else {
            mcvConfirm.visibility = View.VISIBLE
            mcvNewsBar.visibility = View.GONE
            dpAnswerDatePicker.visibility = View.GONE
            mcvAnswerDate.strokeWidth = 0
            mcvAnswerDate.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
        }
    }


    private fun setKeyboardInsets() = binding.run {
        svScroll.setOnClickListener { hideKeyboard() }

        root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.stableInsetBottom
            }

            viewModel.setKeyboardUp(imeHeight != 0)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }

            if (imeHeight == 0) {
                mcvConfirm.visibility = View.VISIBLE
                mcvNewsBar.visibility = View.GONE
                root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
//                svScroll.setPadding(0)
            } else {
                val forgetAgainBottomMargin = requireContext().dpToPx(32f)
                mcvConfirm.visibility = View.GONE
                mcvNewsBar.visibility = View.VISIBLE

                root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
//                svScroll.setPadding(0, 0, 0, forgetAgainBottomMargin)
                lifecycleScope.launch {
                    delay(10)
                    svScroll.smoothScrollBy(0, getNavigationBarHeight() + forgetAgainBottomMargin)
                }
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun hideConfirmButton(isHidden: Boolean) = binding.run {
        if (dpAnswerDatePicker.isVisible) {
            mcvConfirm.visibility = View.GONE
            return@run
        }

        if (isHidden) {
            mcvConfirm.visibility = View.GONE
        } else {
            mcvConfirm.visibility = View.VISIBLE
        }
    }


    private fun enableConfirmButton(enabled: Boolean) = binding.run {
        mcvConfirm.isClickable = enabled
        mcvConfirm.isFocusable = enabled
        mcvConfirm.isEnabled = enabled

        if (enabled) {
            tvConfirm.setBackgroundResource(R.drawable.n_background_button_main)
        } else {
            tvConfirm.setBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }


    private fun changeLockAnswerDateView(deadline: Date) {
        val formatter = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        val str = formatter.format(deadline)
        binding.tvDeadline.text = str
    }

    private fun changeViewWhenKeyboardUp(isUp: Boolean) {
        if (isUp) {
            hideConfirmButton(true)
            enableAnswerDatePicker(false)
        } else {
            hideConfirmButton(false)
            binding.etAnswer.clearFocus()
            viewModel.setLockAnswerFocused(false)
        }
    }

    private fun changeLockQuestionTypeView(lockQuestionType: Int?) = binding.run {
        mcvAnswer.visibility = View.VISIBLE
        mcvAnswerDate.visibility = View.GONE
        when (lockQuestionType) {
            0 -> {
                tvQuestion.setText(R.string.lock_question_item_1)
            }
            1 -> {
                tvQuestion.setText(R.string.lock_question_item_2)
            }
            2 -> {
                tvQuestion.setText(R.string.lock_question_item_3)
                mcvAnswer.visibility = View.GONE
                mcvAnswerDate.visibility = View.VISIBLE
            }
            3 -> {
                tvQuestion.setText(R.string.lock_question_item_4)
            }
            4 -> {
                tvQuestion.setText(R.string.lock_question_item_5)
            }
            else -> {
                tvQuestion.text = null
            }
        }
    }

    private fun changeLockAnswerFocusedView(focused: Boolean) = binding.run {
        if (focused) {
            mcvAnswer.strokeWidth = requireContext().dpToPx(2f).toInt()
            mcvAnswer.setCardBackgroundColor(Color.WHITE)
            ivClear.visibility = View.VISIBLE

            enableAnswerDatePicker(false)
        } else {
            mcvAnswer.strokeWidth = 0
            mcvAnswer.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
            ivClear.visibility = View.GONE
            etAnswer.clearFocus()
        }
    }

    private fun showInvalidWarning(isInvalid: Boolean) = binding.run {
        if (isInvalid) {
            tvInvalidAnswerWarning.setTextColor(requireContext().getColor(R.color.system_error))
            mcvAnswer.strokeColor = requireContext().getColor(R.color.system_error)
            mcvAnswerDate.strokeColor = requireContext().getColor(R.color.system_error)
            mcvAnswer.strokeWidth = requireContext().dpToPx(1f).toInt()
            mcvAnswerDate.strokeWidth = requireContext().dpToPx(1f).toInt()
            mcvAnswer.setCardBackgroundColor(Color.WHITE)
            mcvAnswerDate.setCardBackgroundColor(Color.WHITE)
        } else {
            tvInvalidAnswerWarning.setTextColor(Color.WHITE)
            mcvAnswer.strokeColor = requireContext().getColor(R.color.main_500)
            mcvAnswerDate.strokeColor = requireContext().getColor(R.color.main_500)
        }
    }

    private fun changeLockAnswerView(lockAnswer: String?) = binding.run {
        tvNumTitle.text = lockAnswer?.length?.toString() ?: requireContext().getString(R.string.add_challenge_default_num_title)
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
            launch { viewModel.lockQuestionType.collectLatest(::changeLockQuestionTypeView) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeViewWhenKeyboardUp) }
            launch { viewModel.lockAnswerDate.collectLatest(::changeLockAnswerDateView) }
            launch { viewModel.isLockAnswerFocused.collectLatest(::changeLockAnswerFocusedView) }
            launch { viewModel.isConfirmEnabled.collectLatest(::enableConfirmButton) }
            launch { viewModel.showInvalidWarning.collectLatest(::showInvalidWarning) }
            launch { viewModel.lockAnswer.collectLatest(::changeLockAnswerView) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpAnswerDatePicker.isVisible) {
                    enableAnswerDatePicker(false)
                    hideConfirmButton(false)
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