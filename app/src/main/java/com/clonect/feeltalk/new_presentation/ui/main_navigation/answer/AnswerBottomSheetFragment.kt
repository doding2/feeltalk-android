package com.clonect.feeltalk.new_presentation.ui.main_navigation.answer

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentAnswerBottomSheetBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnswerBottomSheetFragment(
    val onAnswerQuestion: (Question) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AnswerBottomSheetFragment"
    }

    private lateinit var binding: FragmentAnswerBottomSheetBinding
    private val viewModel: AnswerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnswerBottomSheetBinding.inflate(inflater, container, false)
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.skipCollapsed = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initQuestion()
        collectViewModel()
        setKeyboardListeners()

        binding.run {
            etAnswer.addTextChangedListener {
                viewModel.setAnswer(it?.toString() ?: "")
            }

            mcvDone.setOnClickListener { answerDone() }

            mcvPartnerAnswerContainer.setOnClickListener { pokePartner() }
        }

    }

    // TODO
    private fun answerDone() {
        showAnswerConfirmDialog {
            val question = viewModel.question.value ?: return@showAnswerConfirmDialog
            question.myAnswer = viewModel.answer.value
            onAnswerQuestion(question)
            dismiss()
        }
    }

    // TODO 상대에게 콕 찌르기 요청 보내기
    private fun pokePartner() {

        val decorView = dialog?.window?.decorView ?: return
        Snackbar.make(
            decorView,
            requireContext().getString(R.string.answer_poke_partner_snack_bar),
            Snackbar.LENGTH_SHORT
        ).also {
            val view = it.view
            view.setOnClickListener { _ -> it.dismiss() }
            val layoutParams = view.layoutParams as FrameLayout.LayoutParams
            layoutParams.bottomMargin = getNavigationBarHeight()
            view.layoutParams = layoutParams
            it.show()
        }
    }

    private fun initQuestion() {
        val question = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("question", Question::class.java)
        } else {
            arguments?.getSerializable("question") as? Question
        }

        question?.let {
            viewModel.setQuestion(it)
            binding.run {
                tvQuestionHeader.text = it.header
                tvQuestionBody.text = it.body

                val isPartnerAnswered = it.partnerAnswer != null
                if (isPartnerAnswered) {
                    tvPartnerAnswerState.setText(R.string.answer_partner_answer_state_1)
                    mcvPartnerAnswerContainer.apply {
                        setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                        strokeColor = Color.TRANSPARENT
                        strokeWidth = 0
                        isFocusable = false
                        isClickable = false
                        isEnabled = false
                    }
                } else {
                    tvPartnerAnswerState.setText(R.string.answer_partner_answer_state_2)
                    mcvPartnerAnswerContainer.apply {
                        setCardBackgroundColor(requireContext().getColor(R.color.main_300))
                        strokeColor = requireContext().getColor(R.color.main_500)
                        strokeWidth = requireActivity().dpToPx(1f).toInt()
                        isFocusable = true
                        isClickable = true
                        isEnabled = true
                    }
                }

                val isUsersAnswered = it.myAnswer != null
                if (isUsersAnswered) {
                    etAnswer.setText(it.myAnswer)
                    tvDone.setText(R.string.answer_button_modify)
                    tvDone.setTextColor(Color.BLACK)
                    mcvDone.apply {
                        setCardBackgroundColor(Color.WHITE)
                        strokeColor = Color.BLACK
                        strokeWidth = requireActivity().dpToPx(1f).toInt()
                    }
                    viewModel.setEditMode(true)
                    viewModel.setPreviousAnswer(it.myAnswer)
                } else {
                    tvDone.setText(R.string.answer_button_done)
                    tvDone.setTextColor(Color.WHITE)
                    mcvDone.apply {
                        setCardBackgroundColor(requireContext().getColor(R.color.main_400))
                        strokeColor = Color.TRANSPARENT
                        strokeWidth = 0
                    }
                    viewModel.setEditMode(false)
                }
            }
        }
    }

    private fun setKeyboardListeners() = binding.run {
        llClearFocusArea.setOnClickListener { hideKeyboard() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setWindowInsetsAnimationCallback(
                object : WindowInsetsAnimation.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE
                ) {
                    override fun onProgress(insets: WindowInsets, runningAnimations: MutableList<WindowInsetsAnimation>): WindowInsets {
                        val showingKeyboard = requireView().rootWindowInsets.isVisible(WindowInsets.Type.ime())
                        viewModel.setKeyboardUp(showingKeyboard)
                        return insets
                    }
                }
            )
        } else {
            viewModel.setKeyboardUp(true)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etAnswer.windowToken, 0)
    }


    private fun setDoneButtonMargin(margin: Int) = binding.run {
        val params = mcvDone.layoutParams as FrameLayout.LayoutParams
        params.setMargins(margin, 0, margin, 0)
        mcvDone.layoutParams = params
    }

    private fun expandDoneButton(enabled: Boolean) = binding.run {
        if (enabled) {
            setDoneButtonMargin(0)
            mcvDone.radius = 0f
        } else {
            etAnswer.clearFocus()
            setDoneButtonMargin((activity?.dpToPx(20f)?.toInt() ?: 0))
            mcvDone.radius = 500f
        }
    }

    private fun enableDoneButton(enabled: Boolean) = binding.mcvDone.run {
        if (!viewModel.isEditMode.value) {
            val backgroundResource = if (enabled) R.color.main_500 else R.color.main_400
            setCardBackgroundColor(requireContext().getColor(backgroundResource))
        }
        isCheckable = enabled
        isFocusable = enabled
        isEnabled = enabled
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {

            launch { viewModel.isKeyboardUp.collectLatest(::expandDoneButton) }

            launch {
                viewModel.answer.collectLatest {
                    val isButtonEnabled = it.isNotBlank() && it != viewModel.previousAnswer.value
                    enableDoneButton(isButtonEnabled)
                }
            }

        }
    }

}