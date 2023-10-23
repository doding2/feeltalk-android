package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoingDetail

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
import com.clonect.feeltalk.databinding.FragmentOngoingDetailBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.NewsBarItem
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.addChallenge.NewsBarAdapter
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
class OngoingDetailFragment : Fragment() {

    private lateinit var binding: FragmentOngoingDetailBinding
    private val viewModel: OngoingDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOngoingDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        initChallenge()
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCompleteSheet()
        setNewsBar()
        setFocusListener()
        setDatePickerListener()
        setKeyboardInsets()
        setEditTextNestedScroll()
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivEdit.setOnClickListener { viewModel.setEditMode(true) }
            ivDelete.setOnClickListener { deleteChallenge() }


            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }


            mcvDeadline.setOnClickListener { enableDeadlinePicker(true) }

            ivClear.setOnClickListener { etTitle.setText("") }
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            mcvEditRound.setOnClickListener { editChallenge() }

            mcvCompleteRound.setOnClickListener { completeChallenge() }
            tvNext.setOnClickListener { changeFocus() }

            lavCompleteChallenge.setOnClickListener {
                findNavController().popBackStack()
            }
            sheetComplete.mcvCompleteButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initChallenge() {
        val challenge = if (viewModel.challenge.value == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable("challenge", Challenge::class.java)
            } else {
                arguments?.getSerializable("challenge") as? Challenge
            }
        } else {
            viewModel.challenge.value
        }
        if (challenge != null) {
            viewModel.initChallenge(challenge)
            binding.etTitle.setText(challenge.title)
            binding.etBody.setText(challenge.body)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun completeChallenge() {
        viewModel.completeChallenge {
            viewModel.setChallengeCompleted(true)
        }
    }

    private fun editChallenge() {
        hideKeyboard()
        viewModel.editChallenge {
            viewModel.setEditMode(false)
            initChallenge()
        }
    }

    private fun deleteChallenge() {
        showConfirmDialog(
            title = requireContext().getString(R.string.delete_challenge_title),
            body = requireContext().getString(R.string.delete_challenge_body),
            cancelButton = requireContext().getString(R.string.delete_challenge_cancel),
            confirmButton = requireContext().getString(R.string.delete_challenge_confirm),
            onConfirm = {
                viewModel.deleteChallenge {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun setUpCompleteSheet() {
        val behavior = BottomSheetBehavior.from(binding.flCompleteSheet).apply {
            peekHeight = 0
            skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN && viewModel.isChallengeCompleted.value) {
                        findNavController().popBackStack()
                    }
                }
            })
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

                expandEditButton(true)
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

            val isKeyboardUp = imeHeight != 0
            viewModel.setKeyboardUp(isKeyboardUp)
            if (!isKeyboardUp && viewModel.focused.value != "deadline") {
                viewModel.setFocusedEditText(null)
            }

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
        binding.etTitle.clearFocus()
        binding.etBody.clearFocus()
    }

    private fun showKeyboard(target: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(target, 0)
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


    private fun enableEditMode(enabled: Boolean) = binding.run {
        if (enabled) {
            tvDetailTitle1.setText(R.string.add_challenge_title_1)
            tvDetailTitle2.setText(R.string.add_challenge_title_2)

            ivEdit.visibility = View.GONE
            ivDelete.visibility = View.GONE

            etTitle.isEnabled = true
            etBody.isFocusableInTouchMode = true
            mcvDeadline.isEnabled = true
            mcvNewsBar.isEnabled = true
            mcvEditRound.isEnabled = true

            mcvCompleteRound.visibility = View.GONE
            mcvEditRound.visibility = View.VISIBLE
            mcvNewsBar.visibility = View.GONE
        } else {
            tvDetailTitle1.setText(R.string.challenge_detail_title_1)
            tvDetailTitle2.setText(R.string.challenge_detail_title_2)

            ivEdit.visibility = View.VISIBLE
            ivDelete.visibility = View.VISIBLE

            etTitle.isEnabled = false
            etBody.isFocusableInTouchMode = false
            etBody.clearFocus()
            mcvDeadline.isEnabled = false
            mcvNewsBar.isEnabled = false
            mcvEditRound.isEnabled = false

            mcvCompleteRound.visibility = View.VISIBLE
            mcvEditRound.visibility = View.GONE
            mcvNewsBar.visibility = View.GONE

            viewModel.setFocusedEditText(null)
        }
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

    private fun expandEditButton(enabled: Boolean) = binding.run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mcvEditRound.visibility = View.VISIBLE
            mcvEditRound.radius = 0f
            mcvEditRound.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(left = 0, right = 0)
            }
            return@run
        }

        if (dpDeadlinePicker.isVisible) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvEditRound.visibility = View.GONE
            return@run
        }

        if (enabled) {
            mcvNewsBar.visibility = View.VISIBLE
            mcvEditRound.visibility = View.GONE
        } else {
            mcvNewsBar.visibility = View.GONE
            mcvEditRound.visibility = View.VISIBLE
        }
    }

    private fun enableEditButton(enabled: Boolean) = binding.run {
        mcvEditRound.isClickable = enabled
        mcvEditRound.isFocusable = enabled
        mcvEditRound.isEnabled = enabled

        mcvNewsBar.isClickable = enabled
        mcvNewsBar.isFocusable = enabled
        mcvNewsBar.isEnabled = enabled

        if (enabled) {
            tvEditRound.setBackgroundResource(R.drawable.n_background_button_black)
        } else {
            tvEditRound.setBackgroundColor(resources.getColor(R.color.gray_300, null))
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
            expandEditButton(true)
            enableDeadlinePicker(false)
        } else {
            expandEditButton(false)
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

                etTitle.clearFocus()
                etBody.clearFocus()
            }
        }
    }

    private fun changeCompleteChallengeView(isCompleted: Boolean) = binding.run {
        val behavior = BottomSheetBehavior.from(binding.flCompleteSheet)
        if (isCompleted) {
            lavCompleteChallenge.visibility = View.VISIBLE
            lavCompleteChallenge.playAnimation()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            lavCompleteChallenge.visibility = View.GONE
            lavCompleteChallenge.cancelAnimation()
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.isEditMode.collectLatest(::enableEditMode) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeViewWhenKeyboardUp) }
            launch { viewModel.deadline.collectLatest(::changeDeadlineView) }
            launch { viewModel.isEditEnabled.collectLatest(::enableEditButton) }
            launch { viewModel.title.collectLatest(::changeNumTitleView) }
            launch { viewModel.body.collectLatest(::changeNumBodyView) }
            launch { viewModel.focused.collectLatest(::changeFocusView) }
            launch { viewModel.isChallengeCompleted.collectLatest(::changeCompleteChallengeView) }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpDeadlinePicker.isVisible) {
                    enableDeadlinePicker(false)
                    expandEditButton(false)
                    return
                }
                if (viewModel.isEditMode.value) {
                    if (viewModel.isEdited()) {
                        showConfirmDialog(
                            title = requireContext().getString(R.string.challenge_edit_cancel_title),
                            body = requireContext().getString(R.string.challenge_edit_cancel_body),
                            cancelButton = requireContext().getString(R.string.challenge_edit_cancel_cancel),
                            confirmButton = requireContext().getString(R.string.challenge_edit_cancel_confirm),
                            onConfirm = {
                                viewModel.setEditMode(false)
                                initChallenge()
                            }
                        )
                    } else {
                        viewModel.setEditMode(false)
                        initChallenge()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}