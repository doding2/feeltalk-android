package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.addChallenge

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentAddChallengeBinding
import com.clonect.feeltalk.new_domain.model.challenge.NewsBarItem
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@AndroidEntryPoint
class AddChallengeFragment : Fragment() {

    private lateinit var binding: FragmentAddChallengeBinding
    private val viewModel: AddChallengeViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private val loadingDialog by lazy { makeLoadingDialog() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddChallengeBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNewsBar()
        setFocusListener()
        setDatePickerListener()
        setKeyboardInsets()
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }

            mcvDeadline.setOnClickListener { enableDeadlinePicker(true) }

            ivClear.setOnClickListener { etTitle.setText("") }

            mcvAddRound.setOnClickListener { addChallenge() }
            tvNext.setOnClickListener { changeFocus() }
        }
    }


    private fun addChallenge() {
        hideKeyboard()
        viewModel.addNewChallenge {
            findNavController().popBackStack()
        }
    }

    private fun setNewsBar() = binding.run {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvNewsBar)

        rvNewsBar.isClickable = false
        rvNewsBar.isFocusable = false
        rvNewsBar.isNestedScrollingEnabled = false
        rvNewsBar.setOnTouchListener { _, _ -> true }
        rvNewsBar.adapter = NewsBarAdapter(
            listOf(
                NewsBarItem(
                    highlight = requireContext().getString(R.string.news_bar_highlight_1),
                    normal = requireContext().getString(R.string.news_bar_normal_1)
                ),
                NewsBarItem(
                    highlight = requireContext().getString(R.string.news_bar_highlight_2),
                    normal = requireContext().getString(R.string.news_bar_normal_2)
                ),
                NewsBarItem(
                    highlight = requireContext().getString(R.string.news_bar_highlight_3),
                    normal = requireContext().getString(R.string.news_bar_normal_3)
                ),
                NewsBarItem(
                    highlight = requireContext().getString(R.string.news_bar_highlight_4),
                    normal = requireContext().getString(R.string.news_bar_normal_4)
                ),
            )
        )

        val dy = requireContext().dpToPx(34f).toInt()
        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                delay(3000)
                rvNewsBar.smoothScrollBy(0, dy, null, 1000)
            }
        }
    }

    private fun setFocusListener() = binding.run {
        etTitle.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                viewModel.setFocusedEditText("title")
            }
        }
        etBody.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                viewModel.setFocusedEditText("body")
            }
        }
        etTitle.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                mcvDeadline.requestFocus()
                mcvDeadline.performClick()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun enableDeadlinePicker(enabled: Boolean) = binding.run {
        if (enabled) {
            lifecycleScope.launch {
                viewModel.setFocusedEditText("deadline")
                if (viewModel.isKeyboardUp.value) {
                    hideKeyboard()
                    delay(100)
                }
                dpDeadlinePicker.visibility = View.VISIBLE
                mcvDeadline.strokeWidth = activity.dpToPx(2f).toInt()
                mcvDeadline.setCardBackgroundColor(Color.WHITE)

                expandAddButton(true)
            }
        } else {
            dpDeadlinePicker.visibility = View.GONE
            mcvDeadline.strokeWidth = 0
            mcvDeadline.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
        }
    }

    private fun setDatePickerListener() {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val now = viewModel.deadline.value
        val dateString = formatter.format(now)
        val dateTokens = dateString.split("/")

        binding.dpDeadlinePicker.minDate = now.time

        binding.dpDeadlinePicker.init(
            dateTokens[0].toInt(),
            dateTokens[1].toInt() - 1,
            dateTokens[2].toInt()
        ) { picker, year, monthOfYear, dayOfMonth ->
            val str = "$year/${monthOfYear + 1}/$dayOfMonth"
            val date = formatter.parse(str)
            if (date != null) {
                viewModel.setDeadline(date)
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

            if (imeHeight == 0) {
                binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.etTitle.clearFocus()
        binding.etBody.clearFocus()
    }

    private fun showKeyboard(target: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(target, 0)
    }


    private fun changeFocus() = binding.run {
        when (viewModel.focused.value) {
            "title" -> {
                mcvDeadline.requestFocus()
                mcvDeadline.performClick()
            }
            "deadline" -> {
                etBody.requestFocus()
                showKeyboard(etBody)
            }
            "body" -> {
                hideKeyboard()
            }
            else -> {
                hideKeyboard()
            }
        }
    }


    private fun changeNumTitleView(title: String?) = binding.run {
        tvNumTitle.text = title?.length?.toString() ?: requireContext().getString(R.string.add_challenge_default_num_title)
    }

    private fun changeNumBodyView(body: String?) = binding.run {
        tvNumBody.text = body?.length?.toString() ?: requireContext().getString(R.string.add_challenge_default_num_body)
    }


    private fun expandAddButton(enabled: Boolean) = binding.run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mcvAddRound.visibility = View.VISIBLE
            mcvAddRound.radius = 0f
            mcvAddRound.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(left = 0, right = 0)
            }
            return@run
        }

        if (dpDeadlinePicker.isVisible) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvAddRound.visibility = View.GONE
            return@run
        }

        if (enabled) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvAddRound.visibility = View.GONE
        } else {
            mcvNewsBar.visibility = View.GONE
            mcvAddRound.visibility = View.VISIBLE
        }
    }


    private fun enableAddButton(enabled: Boolean) = binding.run {
        mcvAddRound.isClickable = enabled
        mcvAddRound.isFocusable = enabled
        mcvAddRound.isEnabled = enabled

        mcvNewsBar.isClickable = enabled
        mcvNewsBar.isFocusable = enabled
        mcvNewsBar.isEnabled = enabled

        if (enabled) {
            mcvAddRound.setCardBackgroundColor(resources.getColor(R.color.main_500, null))
        } else {
            mcvAddRound.setCardBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }

    private fun changeDeadlineView(deadline: Date) {
        val dDay = ceil((deadline.time - Date().time).toDouble() / Constants.ONE_DAY).toInt()
        val formatter = SimpleDateFormat("yyyy년 M월 dd일까지", Locale.getDefault())
        val str = formatter.format(deadline)
        binding.tvDeadline.text = str
        binding.tvDDay.text = if (dDay >= 999) {
            requireContext().getString(R.string.add_challenge_d_day_over)
        } else if (dDay == 0) {
            requireContext().getString(R.string.add_challenge_d_day_today)
        } else {
            requireContext().getString(R.string.add_challenge_d_day_normal) + dDay
        }
    }

    private fun changeViewWhenKeyboardUp(isUp: Boolean) {
        if (isUp) {
            expandAddButton(true)
            enableDeadlinePicker(false)
        } else {
            expandAddButton(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun changeFocusView(focused: String?) = binding.run {
        when (focused) {
            "title" -> {
                mcvTitle.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvTitle.setCardBackgroundColor(Color.WHITE)
                ivClear.visibility = View.VISIBLE

                enableDeadlinePicker(false)

                mcvBody.strokeWidth = 0
                mcvBody.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                tvNext.setText(R.string.add_challenge_next)
            }
            "deadline" -> {
                mcvTitle.strokeWidth = 0
                mcvTitle.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivClear.visibility = View.GONE

                enableDeadlinePicker(true)

                mcvBody.strokeWidth = 0
                mcvBody.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                tvNext.setText(R.string.add_challenge_next)
            }
            "body" -> {
                mcvTitle.strokeWidth = 0
                mcvTitle.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivClear.visibility = View.GONE

                enableDeadlinePicker(false)

                mcvBody.strokeWidth = requireContext().dpToPx(2f).toInt()
                mcvBody.setCardBackgroundColor(Color.WHITE)

                tvNext.setText(R.string.add_challenge_done)
            }
            else -> {
                mcvTitle.strokeWidth = 0
                mcvTitle.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                ivClear.visibility = View.GONE

                enableDeadlinePicker(false)

                mcvBody.strokeWidth = 0
                mcvBody.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

                tvNext.setText(R.string.add_challenge_next)
            }
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeViewWhenKeyboardUp) }
            launch { viewModel.deadline.collectLatest(::changeDeadlineView) }
            launch { viewModel.isAddEnabled.collectLatest(::enableAddButton) }
            launch { viewModel.title.collectLatest(::changeNumTitleView) }
            launch { viewModel.body.collectLatest(::changeNumBodyView) }
            launch { viewModel.focused.collectLatest(::changeFocusView) }
        }
    }

    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpDeadlinePicker.isVisible) {
                    enableDeadlinePicker(false)
                    expandAddButton(false)
                    return
                }
                if (viewModel.isEdited()) {
                    showConfirmDialog(
                        title = requireContext().getString(R.string.challenge_add_cancel_title),
                        body = requireContext().getString(R.string.challenge_add_cancel_body),
                        cancelButton = requireContext().getString(R.string.challenge_add_cancel_cancel),
                        confirmButton = requireContext().getString(R.string.challenge_add_cancel_confirm),
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