package com.clonect.feeltalk.new_presentation.ui.mainNavigation.answer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
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
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AnswerFragment : Fragment() {

    private lateinit var binding: FragmentAnswerBinding
    private val viewModel: AnswerViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnswerBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
        }
        setKeyboardInsets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cancelJob()
        viewModel.setJob(
            collectViewModel()
        )

        binding.run {
            ivDismissDialog.setOnClickListener {
                navViewModel.setShowAnswerSheet(false)
            }
            etMyAnswer.addTextChangedListener {
                viewModel.setAnswer(it?.toString() ?: "")
            }

            mcvDoneRound.setOnClickListener {
                if (viewModel.isReadMode.value) showChatBottomSheet()
                else answerDone()
            }
            mcvDoneSquare.setOnClickListener {
                if (viewModel.isReadMode.value) showChatBottomSheet()
                else answerDone()
            }

            mcvPressForAnswer.setOnClickListener { pokePartner() }
        }
    }

    // TODO
    private fun answerDone() {
        showAnswerConfirmDialog {
            val question = viewModel.question.value ?: return@showAnswerConfirmDialog
            question.myAnswer = viewModel.answer.value
            navViewModel.setShowAnswerSheet(false)
//            onAnswerQuestion(question)
            Toast.makeText(requireContext(), "답변 완료", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showChatBottomSheet() {
        navViewModel.setShowAnswerSheet(false)
        navViewModel.setShowChatNavigation(true)
    }

    // TODO 상대에게 콕 찌르기 요청 보내기
    private fun pokePartner() {
        val decorView = activity?.window?.decorView ?: return
        Snackbar.make(
            decorView,
            requireContext().getString(R.string.answer_poke_partner_snack_bar),
            Snackbar.LENGTH_SHORT
        ).also {
            val view = it.view
            view.setOnClickListener { _ -> it.dismiss() }
            it.show()
        }
    }

    private fun setQuestion(question: Question) {
        question.also {
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
                    etMyAnswer.text = null
                    etMyAnswer.isEnabled = true
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
                    } else {
                        mcvPartnerNotDone.visibility = View.GONE
                        mcvPartnerDone.visibility = View.VISIBLE
                        etPartnerAnswer.visibility = View.GONE

                        mcvDoneRound.visibility = View.VISIBLE
                        mcvDoneSquare.visibility = View.GONE
                    }
                }
                else {
                    if (viewModel.isReadMode.value) {
                        mcvPartnerNotDone.visibility = View.VISIBLE
                        mcvPartnerDone.visibility = View.GONE
                        etPartnerAnswer.visibility = View.GONE

                        mcvDoneRound.visibility = View.GONE
                        mcvDoneSquare.visibility = View.GONE
                    } else {
                        mcvPartnerNotDone.visibility = View.VISIBLE
                        mcvPartnerDone.visibility = View.GONE
                        etPartnerAnswer.visibility = View.GONE

                        mcvDoneRound.visibility = View.VISIBLE
                        mcvDoneSquare.visibility = View.GONE
                    }
                }
            }
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
                insets.systemWindowInsetBottom
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
        imm.hideSoftInputFromWindow(binding.etMyAnswer.windowToken, 0)
    }


    private fun expandDoneButton(enabled: Boolean) = binding.run {
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
        }

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
            launch {
                navViewModel.answerTargetQuestion.collectLatest {
                    if (it == null) {
                        viewModel.clear()
                        hideKeyboard()
                        binding.etMyAnswer.clearFocus()
                    } else {
                        setQuestion(it)
                    }
                }
            }

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