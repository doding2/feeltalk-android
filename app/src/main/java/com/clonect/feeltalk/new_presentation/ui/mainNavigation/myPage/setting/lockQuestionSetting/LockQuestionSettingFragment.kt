package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockQuestionSetting

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentLockQuestionSettingBinding
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class LockQuestionSettingFragment : Fragment() {


    private lateinit var binding: FragmentLockQuestionSettingBinding
    private val viewModel: LockQuestionSettingViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLockQuestionSettingBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        val password = arguments?.getString("password")
        viewModel.setPassword(password)
        if (password == null) {
            onBackCallback.handleOnBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        initLockQuestion()
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
            mcvAnswerDate.setOnClickListener { enableAnswerDatePicker(true) }
            tvNext.setOnClickListener { doneNewsBar() }

            mcvSubmit.setOnClickListener { submitLockQuestionSetting() }
        }
    }

    private fun submitLockQuestionSetting() {
        navigateToLockSetting()
    }

    private fun navigateToLockSetting() {
        showSuccessSnackBar()
        setFragmentResult(
            requestKey = "passwordSettingFragment",
            result = bundleOf("lockEnabled" to true)
        )
        findNavController().popBackStack()
    }

    private fun doneNewsBar() {
        lifecycleScope.launch {
            if (viewModel.isKeyboardUp.value) {
                hideKeyboard()
                delay(100)
            }
            enableAnswerDatePicker(false)
            expandAddButton(false)
        }
    }

    private fun showSuccessSnackBar() {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = requireContext().getString(R.string.lock_question_setting_success_snack_bar),
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun initLockQuestion() = binding.spinnerLockQuestion.run {
        val adapter = LockQuestionAdapter(
            spinnerView = this,
            items = listOf(
                requireContext().getString(R.string.lock_question_item_1),
                requireContext().getString(R.string.lock_question_item_2),
                requireContext().getString(R.string.lock_question_item_3)
            )
        ) { oldIndex, oldItem, newIndex, newItem ->
            viewModel.setQuestionType(newIndex)
        }
        setSpinnerAdapter(adapter)
        getSpinnerRecyclerView().layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lifecycleOwner = this@LockQuestionSettingFragment
        setOnSpinnerOutsideTouchListener { view, motionEvent ->
            dismiss()
        }
        setOnClickListener {
            doneNewsBar()
            showOrDismiss()
            setSpinnerSelected(isShowing)
        }
        setOnSpinnerDismissListener {
            setSpinnerSelected(false)
        }
    }

    private fun setSpinnerSelected(selected: Boolean) = binding.spinnerLockQuestion.run {
        if (selected) {
            setBackgroundResource(R.drawable.n_background_spinner_selected)
            setHint(R.string.lock_question_hint_selected)
            setHintTextColor(Color.BLACK)
        } else {
            setBackgroundResource(R.drawable.n_background_spinner_unselected)
            setHint(R.string.lock_question_hint_unselected)
            setHintTextColor(requireContext().getColor(R.color.gray_400))
        }
    }


    private fun enableAnswerDatePicker(enabled: Boolean) = binding.run {
        if (enabled) {
            lifecycleScope.launch {
                if (viewModel.isKeyboardUp.value) {
                    hideKeyboard()
                    delay(100)
                }
                dpAnswerDatePicker.visibility = View.VISIBLE
                mcvAnswerDate.strokeWidth = activity.dpToPx(2f).toInt()
                mcvAnswerDate.setCardBackgroundColor(Color.WHITE)

                expandAddButton(true)
            }
        } else {
            dpAnswerDatePicker.visibility = View.GONE
            mcvAnswerDate.strokeWidth = 0
            mcvAnswerDate.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
        }
    }

    private fun setDatePickerListener() {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val now = viewModel.lockAnswerDate.value
        val dateString = formatter.format(now)
        val dateTokens = dateString.split("/")

        binding.dpAnswerDatePicker.maxDate = now.time

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

    private fun setKeyboardInsets() = binding.run {
        svScroll.setOnClickListener { hideKeyboard() }

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.systemWindowInsetBottom
            }

            viewModel.setKeyboardUp(imeHeight != 0)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }

            val horizontalPadding = requireContext().dpToPx(20f).toInt()
            if (imeHeight == 0) {
                binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
                binding.llContent.setPadding(horizontalPadding, 0, horizontalPadding, 0)
                viewModel.setLockAnswerFocused(false)
            } else {
                val bottomPadding = requireContext().dpToPx(56f).toInt()

                binding.root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
                binding.llContent.setPadding(horizontalPadding, 0, horizontalPadding, bottomPadding)
                lifecycleScope.launch {
                    delay(100)
                    svScroll.smoothScrollBy(0, bottomPadding)
                }
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun expandAddButton(enabled: Boolean) = binding.run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mcvSubmit.visibility = View.VISIBLE
            mcvSubmit.radius = 0f
            mcvSubmit.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(left = 0, right = 0)
            }
            return@run
        }

        if (dpAnswerDatePicker.isVisible) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvSubmit.visibility = View.GONE
            return@run
        }

        if (enabled) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvSubmit.visibility = View.GONE
        } else {
            mcvNewsBar.visibility = View.GONE
            mcvSubmit.visibility = View.VISIBLE
        }
    }

    private fun enableAddButton(enabled: Boolean) = binding.run {
        mcvSubmit.isClickable = enabled
        mcvSubmit.isFocusable = enabled
        mcvSubmit.isEnabled = enabled

        mcvNewsBar.isClickable = enabled
        mcvNewsBar.isFocusable = enabled
        mcvNewsBar.isEnabled = enabled

        if (enabled) {
            mcvSubmit.setCardBackgroundColor(resources.getColor(R.color.main_500, null))
        } else {
            mcvSubmit.setCardBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }

    private fun changeLockAnswerDateView(deadline: Date) {
        val formatter = SimpleDateFormat("yyyy년 M월 dd일", Locale.getDefault())
        val str = formatter.format(deadline)
        binding.tvDeadline.text = str
    }

    private fun changeViewWhenKeyboardUp(isUp: Boolean) {
        if (isUp) {
            expandAddButton(true)
            enableAnswerDatePicker(false)
        } else {
            expandAddButton(false)
        }
    }



    private fun changeQuestionTypeView(questionType: Int?) = binding.run {
        when (questionType) {
            null -> {
                mcvAnswer.visibility = View.VISIBLE
                mcvAnswerDate.visibility = View.GONE
                return@run
            }
            0 -> {
                mcvAnswer.visibility = View.VISIBLE
                mcvAnswerDate.visibility = View.GONE
                spinnerLockQuestion.text = requireContext().getString(R.string.lock_question_item_1)
            }
            1 -> {
                mcvAnswer.visibility = View.GONE
                mcvAnswerDate.visibility = View.VISIBLE
                spinnerLockQuestion.text = requireContext().getString(R.string.lock_question_item_2)
            }
            2 -> {
                mcvAnswer.visibility = View.VISIBLE
                mcvAnswerDate.visibility = View.GONE
                spinnerLockQuestion.text = requireContext().getString(R.string.lock_question_item_3)
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


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.questionType.collectLatest(::changeQuestionTypeView) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeViewWhenKeyboardUp) }
            launch { viewModel.lockAnswerDate.collectLatest(::changeLockAnswerDateView) }
            launch { viewModel.isLockAnswerFocused.collectLatest(::changeLockAnswerFocusedView) }
            launch { viewModel.isAddEnabled.collectLatest(::enableAddButton) }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpAnswerDatePicker.isVisible) {
                    enableAnswerDatePicker(false)
                    expandAddButton(false)
                    return
                }
                if (binding.spinnerLockQuestion.isShowing) {
                    binding.spinnerLockQuestion.dismiss()
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