package com.clonect.feeltalk.new_presentation.ui.mainNavigation.question.answer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentAnswerBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AnswerFragment : Fragment() {

    private lateinit var binding: FragmentAnswerBinding
    private val viewModel: AnswerViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private var viewModelJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnswerBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
        }
        setKeyboardInsets()
        setEditTextScroll()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivDismissDialog.setOnClickListener {
                if (navViewModel.isUserAnswering.value) {
                    showConfirmDialog(
                        title = requireContext().getString(R.string.answer_cancel_title),
                        body = requireContext().getString(R.string.answer_cancel_body),
                        confirmButton = requireContext().getString(R.string.answer_cancel_confirm),
                    ) {
                        navViewModel.setShowAnswerSheet(false)
                    }
                } else {
                    navViewModel.setShowAnswerSheet(false)
                }
            }

            etAnswer.addTextChangedListener {
                val answer = it?.toString() ?: ""
                viewModel.setAnswer(answer)
                navViewModel.setUserAnswering(!viewModel.isReadMode.value && answer.isNotEmpty())
            }

            mcvDoneRound.setOnClickListener {
                if (viewModel.isReadMode.value) showChatBottomSheet()
                else answerQuestion()
            }
            mcvDoneSquare.setOnClickListener {
                if (viewModel.isReadMode.value) showChatBottomSheet()
                else answerQuestion()
            }

            mcvPressForAnswer.setOnClickListener { pokePartner() }
        }
    }

    private fun answerQuestion() {
        showConfirmDialog(
            title = requireContext().getString(R.string.answer_confirm_title),
            body = requireContext().getString(R.string.answer_confirm_body),
            confirmButton = requireContext().getString(R.string.answer_confirm_confirm),
        ) {
            viewModel.answerQuestion(requireContext()) {
                showChatBottomSheet()
            }
        }
    }

    private fun showChatBottomSheet() {
        navViewModel.setShowAnswerSheet(false)
        navViewModel.setShowChatNavigation(true)
    }

    private fun pokePartner() {
        viewModel.pressForAnswer(requireContext())
    }

    private fun changeQuestionView(question: Question?) {
        if (question == null) {
            navViewModel.setUserAnswering(false)
            return
        }

        question.also {
            binding.run {
                val spanBody = SpannableString(question.body).apply {
                    val mainColor = requireContext().getColor(R.color.main_500)
                    val headerLength = question.header.length
                    question.highlight
                        .map { index ->
                            index - headerLength
                        }.forEach { index ->
                            runCatching {
                                if (index >= question.body.length) return@forEach
                                setSpan(ForegroundColorSpan(mainColor), index.toInt() - 1, index.toInt(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                            }.onFailure {
                                infoLog("Question highlight index error: highlight list: ${question.highlight}\n${it.stackTrace.joinToString("\n")}")
                            }
                        }
                }
                tvQuestionBody.text = spanBody
                tvQuestionHeader.text = it.header

                val isUserAnswered = it.myAnswer != null
                viewModel.setReadMode(isUserAnswered)

                if (viewModel.isReadMode.value) {
                    etAnswer.setText(it.myAnswer)
                    etAnswer.isEnabled = false
                    tvDoneRound.setText(R.string.answer_button_chat)
                    tvDoneSquare.setText(R.string.answer_button_chat)
                } else {
                    // 앱 백그라운드 갔다가 돌아오면 작성된 내용 날아가는거 방지
                    if (!navViewModel.isUserAnswering.value) {
                        etAnswer.text = null
                    }
                    etAnswer.isEnabled = true
                    tvDoneRound.setText(R.string.answer_button_done)
                    tvDoneSquare.setText(R.string.answer_button_done)
                }


                val isPartnerAnswered = it.partnerAnswer != null

                if (isPartnerAnswered) {
                    if (viewModel.isReadMode.value) {
                        mcvPartnerNotDone.visibility = View.GONE
                        mcvPartnerDone.visibility = View.GONE
                        etPartnerAnswer.visibility = View.VISIBLE
                        etPartnerAnswer.setText(it.partnerAnswer ?: "")

                        mcvDoneRound.visibility = View.VISIBLE
                        mcvDoneSquare.visibility = View.GONE

                        llNumAnswerContainer.visibility = View.GONE
                    } else {
                        mcvPartnerNotDone.visibility = View.GONE
                        mcvPartnerDone.visibility = View.VISIBLE
                        etPartnerAnswer.visibility = View.GONE

                        mcvDoneRound.visibility = View.VISIBLE
                        mcvDoneSquare.visibility = View.GONE

                        llNumAnswerContainer.visibility = View.VISIBLE
                    }
                }
                else {
                    if (viewModel.isReadMode.value) {
                        mcvPartnerNotDone.visibility = View.VISIBLE
                        mcvPartnerDone.visibility = View.GONE
                        etPartnerAnswer.visibility = View.GONE

                        mcvDoneRound.visibility = View.GONE
                        mcvDoneSquare.visibility = View.GONE

                        llNumAnswerContainer.visibility = View.GONE
                    } else {
                        mcvPartnerNotDone.visibility = View.VISIBLE
                        mcvPartnerDone.visibility = View.GONE
                        etPartnerAnswer.visibility = View.GONE

                        mcvDoneRound.visibility = View.VISIBLE
                        mcvDoneSquare.visibility = View.GONE

                        llNumAnswerContainer.visibility = View.VISIBLE
                    }
                }


                val isButtonEnabled = (isUserAnswered && isPartnerAnswered) || viewModel.answer.value.isNotBlank()
                enableDoneButton(isButtonEnabled)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEditTextScroll() = binding.run {
//        var isMyEtInTop = false
//        var isMyEtInBottom = false
//
//        etMyAnswer.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            isMyEtInTop = !etMyAnswer.canScrollVertically(-10)
//            isMyEtInBottom = !etMyAnswer.canScrollVertically(10)
//        }

        etAnswer.setOnTouchListener { v, event ->
            if (etAnswer.lineCount <= 4) return@setOnTouchListener false

            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }


//        var isPartnerEtInTop = false
//        var isPartnerEtInBottom = false
//
//        etPartnerAnswer.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            isPartnerEtInTop = !etPartnerAnswer.canScrollVertically(-10)
//            isPartnerEtInBottom = !etPartnerAnswer.canScrollVertically(10)
//        }

        etPartnerAnswer.setOnTouchListener { v, event ->
            if (etPartnerAnswer.lineCount <= 4) return@setOnTouchListener false

            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    private fun setKeyboardInsets() = binding.run {
        llClearFocusArea.setOnClickListener {
            hideKeyboard()
        }

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
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
                binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, 0, 0, imeHeight)
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etAnswer.windowToken, 0)
    }


    private fun applyKeyboardUpChanges(isKeyboardUp: Boolean) = binding.run {
        val isPartnerAnswered = viewModel.question.value?.partnerAnswer != null
        val isUserAnswered = viewModel.isReadMode.value
        if (isUserAnswered && !isPartnerAnswered) {
            mcvDoneSquare.visibility = View.GONE
            mcvDoneRound.visibility = View.GONE
            return@run
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mcvDoneRound.updateLayoutParams<RelativeLayout.LayoutParams> {
                updateMargins(left = 0, right = 0)
            }
            mcvDoneRound.radius = 0f
        }

        if (isKeyboardUp) {
            mcvAnswer.strokeWidth = requireContext().dpToPx(2f)
            mcvAnswer.setCardBackgroundColor(Color.WHITE)

            mcvDoneSquare.visibility = View.VISIBLE
            mcvDoneRound.visibility = View.GONE
        } else {
            mcvAnswer.strokeWidth = 0
            mcvAnswer.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))

            etAnswer.clearFocus()
            mcvDoneSquare.visibility = View.GONE
            mcvDoneRound.visibility = View.VISIBLE
        }
    }

    private fun enableDoneButton(enabled: Boolean) = binding.run {
        tvDoneRound.apply {
            if (enabled) {
                setBackgroundResource(R.drawable.n_background_button_main)
            } else {
                setBackgroundColor(requireContext().getColor(R.color.main_400))
            }
        }
        mcvDoneRound.apply {
            isCheckable = enabled
            isFocusable = enabled
            isEnabled = enabled
        }

        tvDoneSquare.apply {
            if (enabled) {
                setBackgroundResource(R.drawable.n_background_button_main)
            } else {
                setBackgroundColor(requireContext().getColor(R.color.main_400))
            }
        }
        mcvDoneSquare.apply {
            isCheckable = enabled
            isFocusable = enabled
            isEnabled = enabled
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

    private fun applyAnswerChanges(answer: String) = binding.run {
        tvNumAnswer.text = answer.length.toString()
    }

    private fun collectViewModel() {
        viewModelJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.message.collect(::showSnackBar) }
                launch { viewModel.question.collectLatest(::changeQuestionView) }
                launch { viewModel.answer.collectLatest(::applyAnswerChanges) }

                launch {
                    navViewModel.answerTargetQuestion.collectLatest {
                        if (it == null) {
                            viewModel.clear()
                            hideKeyboard()
                            binding.etAnswer.clearFocus()
                        } else {
                            viewModel.setQuestion(it)
                        }
                    }
                }

                launch { viewModel.isKeyboardUp.collectLatest(::applyKeyboardUpChanges) }

                launch {
                    viewModel.answer.collectLatest {
                        val isButtonEnabled = if (viewModel.isReadMode.value) {
                            viewModel.question.value?.partnerAnswer != null
                                    && viewModel.question.value?.myAnswer != null
                        } else {
                            it.isNotBlank()
                        }
                        enableDoneButton(isButtonEnabled)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelJob?.cancel()
    }
}