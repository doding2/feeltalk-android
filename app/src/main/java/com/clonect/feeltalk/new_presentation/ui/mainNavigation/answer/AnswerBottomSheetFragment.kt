package com.clonect.feeltalk.new_presentation.ui.mainNavigation.answer

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
import androidx.navigation.fragment.findNavController
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
            etMyAnswer.addTextChangedListener {
                viewModel.setAnswer(it?.toString() ?: "")
            }

            mcvDoneRound.setOnClickListener {
                if (viewModel.isReadMode.value) navigateToChat()
                else answerDone()
            }
            mcvDoneSquare.setOnClickListener {
                if (viewModel.isReadMode.value) navigateToChat()
                else answerDone()
            }

            mcvPartnerAnswerStateContainer.setOnClickListener { pokePartner() }
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

    private fun navigateToChat() {
        findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_chatFragment)
        dismiss()
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

                val isUsersAnswered = it.myAnswer != null
                viewModel.setReadMode(isUsersAnswered)

                if (viewModel.isReadMode.value) {
                    etMyAnswer.setText(it.myAnswer)
                    etMyAnswer.isEnabled = false
                    tvDoneRound.setText(R.string.answer_button_chat)
                    tvDoneSquare.setText(R.string.answer_button_chat)
                } else {
                    etMyAnswer.isEnabled = true
                    tvDoneSquare.setText(R.string.answer_button_done)
                }


                val isPartnerAnswered = it.partnerAnswer != null
                if (isPartnerAnswered) {
                    if (viewModel.isReadMode.value) {
                        mcvPartnerAnswerStateContainer.visibility = View.GONE
                        etPartnerAnswer.visibility = View.VISIBLE
                        etPartnerAnswer.setText(it.partnerAnswer ?: "")
                    } else {
                        mcvPartnerAnswerStateContainer.visibility = View.VISIBLE
                        etPartnerAnswer.visibility = View.GONE
                        tvPartnerAnswerState.setText(R.string.answer_partner_answer_state_1)
                        mcvPartnerAnswerStateContainer.apply {
                            setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
                            strokeColor = Color.TRANSPARENT
                            strokeWidth = 0
                            isFocusable = false
                            isClickable = false
                            isEnabled = false
                        }
                    }
                } else {
                    mcvPartnerAnswerStateContainer.visibility = View.VISIBLE
                    etPartnerAnswer.visibility = View.GONE
                    tvPartnerAnswerState.setText(R.string.answer_partner_answer_state_2)
                    mcvPartnerAnswerStateContainer.apply {
                        setCardBackgroundColor(requireContext().getColor(R.color.main_300))
                        strokeColor = requireContext().getColor(R.color.main_500)
                        strokeWidth = requireActivity().dpToPx(1f).toInt()
                        isFocusable = true
                        isClickable = true
                        isEnabled = true
                    }
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
        imm.hideSoftInputFromWindow(binding.etMyAnswer.windowToken, 0)
    }


    private fun expandDoneButton(enabled: Boolean) = binding.run {
        if (enabled) {
            mcvDoneSquare.visibility = View.VISIBLE
            mcvDoneRound.visibility = View.GONE
        } else {
            etMyAnswer.clearFocus()
            mcvDoneSquare.visibility = View.GONE
            mcvDoneRound.visibility = View.VISIBLE
        }
    }

    private fun enableDoneButton(enabled: Boolean) = binding.run {
        mcvDoneRound.apply {
            val backgroundResource = if (enabled) R.color.main_500 else R.color.main_400
            setCardBackgroundColor(requireContext().getColor(backgroundResource))
            isCheckable = enabled
            isFocusable = enabled
            isEnabled = enabled
        }
        mcvDoneSquare.apply {
            val backgroundResource = if (enabled) R.color.main_500 else R.color.main_400
            setCardBackgroundColor(requireContext().getColor(backgroundResource))
            isCheckable = enabled
            isFocusable = enabled
            isEnabled = enabled
        }
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {

            launch { viewModel.isKeyboardUp.collectLatest(::expandDoneButton) }

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