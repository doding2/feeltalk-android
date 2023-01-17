package com.clonect.feeltalk.presentation.ui.today_question

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clonect.feeltalk.R
import com.clonect.feeltalk.data.util.Result
import com.clonect.feeltalk.databinding.FragmentTodayQuestionBinding
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.presentation.util.addTextGradient
import com.clonect.feeltalk.presentation.util.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodayQuestionFragment : Fragment() {

    private lateinit var binding: FragmentTodayQuestionBinding
    private val viewModel: TodayQuestionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTodayQuestionBinding.inflate(inflater, container, false)

        val args: TodayQuestionFragmentArgs by navArgs()
        args.selectedQuestion?.let { viewModel.setQuestion(it) }

        initMyAnswerEditText()

        changeTitleTextByQuestion()

        binding.apply {

            btnBack.setOnClickListener {
                // TODO back button
            }

            etMyAnswer.addTextChangedListener {

                viewModel.setMyAnswer(it.toString().trim())

                val isEnterAnswerButtonReady = (viewModel.questionStateFlow.value is Result.Success)
                        && !it.isNullOrBlank()

                enableEnterAnswerButton(isEnterAnswerButtonReady)
            }

            btnEnterAnswer.setOnClickListener {
                clickEnterAnswerButton()
            }

        }

        return binding.root
    }

    private fun initMyAnswerEditText() = lifecycleScope.launchWhenStarted {
        val myAnswer = viewModel.myAnswerStateFlow.value
        binding.etMyAnswer.setText(myAnswer)

        val enabled = myAnswer.isNotEmpty()
        enableEnterAnswerButton(enabled)
    }

    private fun clickEnterAnswerButton() = lifecycleScope.launch {
        val question = (viewModel.questionStateFlow.value as? Result.Success
            ?: return@launch)
            .data

        var titleId = R.string.dialog_partner_state_not_done_title
        var messageId = R.string.dialog_partner_state_not_done_message
        var confirmId = R.string.dialog_partner_state_not_done_confirm

        val isPartnerReady = !question.partnerAnswer.isNullOrEmpty()
        if (isPartnerReady) {
            titleId = R.string.dialog_partner_state_already_done_title
            messageId = R.string.dialog_partner_state_already_done_message
            confirmId = R.string.dialog_partner_state_already_done_confirm
        }

        showAlertDialog(
            title = getString(titleId),
            message = getString(messageId),
            confirmButtonText = getString(confirmId),
            onConfirmClick = {
                if (question.partnerAnswer.isNullOrEmpty()) {
                    requestPartnerAnswer()
                }
                navigateToChatPage(question)
            }
        )
    }

    private fun requestPartnerAnswer() = lifecycleScope.launch {
        viewModel.requestPartnerAnswer()
        Toast.makeText(requireContext(), "상대방에게 답변을 요청했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToChatPage(question: Question) {
        val bundle = bundleOf("selectedQuestion" to question)

        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(
                R.id.action_todayQuestionFragment_to_chatFragment,
                bundle
            )
    }

    private fun enableEnterAnswerButton(enabled: Boolean) {
        binding.btnEnterAnswer.apply {
            val colorId =
                if (enabled) R.color.today_question_enter_answer_button_enabled_color
                else R.color.today_question_enter_answer_button_disabled_color

            setCardBackgroundColor(ResourcesCompat.getColor(resources, colorId, null))
            isClickable = enabled
            isFocusable = enabled
        }
    }

    private fun changeTitleTextByQuestion() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.questionStateFlow.collectLatest {
                val question = (it as? Result.Success
                    ?: return@collectLatest)
                    .data

                reassembleQuestionContentViews(question)
                changePartnerStateTextView(question)

                if (viewModel.myAnswerStateFlow.value != "")
                    enableEnterAnswerButton(true)
            }
        }
    }

    private fun processQuestion() {

    }

    private fun changePartnerStateTextView(question: Question) = binding.apply {
        if (question.partnerAnswer.isNullOrEmpty()) {
            textPartnerStatePrefix.setText(R.string.today_question_partner_state_not_done_prefix)
            val stateText = getString(R.string.today_question_partner_state_not_done)
            val stateUnderLine = SpannableString(stateText).apply {
                setSpan(UnderlineSpan(), 0, this.length, 0)
            }
            textPartnerState.text = stateUnderLine
            textPartnerStateEmoji.setText(R.string.today_question_partner_state_not_done_emoji)
            return@apply
        }

        textPartnerStatePrefix.setText(R.string.today_question_partner_state_already_done_prefix)
        val stateText = getString(R.string.today_question_partner_state_already_done)
        val stateUnderLine = SpannableString(stateText).apply {
            setSpan(UnderlineSpan(), 0, this.length, 0)
        }
        textPartnerState.text = stateUnderLine
        textPartnerStateEmoji.setText(R.string.today_question_partner_state_already_done_emoji)
    }

    private fun reassembleQuestionContentViews(question: Question) {
        val prefixWordList = question.contentPrefix.split(" ")
        val contentWordList = question.content.split(" ")
        val suffixWordList = question.contentSuffix.split(" ")

        binding.layoutQuestionContent.removeAllViewsInLayout()

        prefixWordList.forEachIndexed { index, word ->
            prefixWordList.last()
            val prefixTextView = LayoutInflater.from(requireContext())
                .inflate(R.layout.text_view_question_content_prefix, null) as TextView
            prefixTextView.text =
                if (index == prefixWordList.lastIndex && word != " ") word else "$word "
            Log.i("TodayQuestionFragment", prefixTextView.text.toString())
            binding.layoutQuestionContent.addView(prefixTextView)
        }

        contentWordList.forEachIndexed { index, word ->
            val contentTextView = LayoutInflater.from(requireContext())
                .inflate(R.layout.text_view_question_content, null) as TextView
            contentTextView.text =
                if (index == contentWordList.lastIndex && word != " ") word else "$word "
            Log.i("TodayQuestionFragment", contentTextView.text.toString())
            contentTextView.addTextGradient()
            binding.layoutQuestionContent.addView(contentTextView)
        }

        suffixWordList.forEachIndexed { index, word ->
            val suffixTextView = LayoutInflater.from(requireContext())
                .inflate(R.layout.text_view_question_content_prefix, null) as TextView
            suffixTextView.text =
                if (index == suffixWordList.lastIndex && word != " ") word else "$word "
            Log.i("TodayQuestionFragment", suffixTextView.text.toString())
            binding.layoutQuestionContent.addView(suffixTextView)
        }
    }

}