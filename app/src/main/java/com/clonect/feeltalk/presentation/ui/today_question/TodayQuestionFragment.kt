package com.clonect.feeltalk.presentation.ui.today_question

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
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
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.databinding.FragmentTodayQuestionBinding
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.presentation.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodayQuestionFragment : Fragment() {

    private lateinit var binding: FragmentTodayQuestionBinding
    private val viewModel: TodayQuestionViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTodayQuestionBinding.inflate(inflater, container, false)

        initMyAnswerEditText()
        collectQuestion()

        binding.apply {
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            etMyAnswer.addTextChangedListener {
                viewModel.setMyAnswer(it.toString().trim())
                val isEnterAnswerButtonReady = viewModel.questionStateFlow.value.question.isNotBlank() && !it.isNullOrBlank()
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
        val question = viewModel.questionStateFlow.value

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

    private fun collectQuestion() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.questionStateFlow.collectLatest {
                reassembleQuestionTitle(it)
                changePartnerStateTextView(it)

                if (viewModel.myAnswerStateFlow.value != "")
                    enableEnterAnswerButton(true)
            }
        }
    }

    private fun changePartnerStateTextView(question: Question) = binding.apply {
        if (!question.partnerAnswer.isNullOrBlank()) {
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

    private fun reassembleQuestionTitle(question: Question) {
        binding.layoutQuestionContent.removeAllViewsInLayout()

//        question.contentPrefix.reassembleTextView(R.layout.text_view_question_content_prefix)
        question.question.reassembleTextView(R.layout.text_view_question_content)
//        question.contentSuffix.reassembleTextView(R.layout.text_view_question_content_suffix)
    }

    private fun String.reassembleTextView(@LayoutRes resource: Int) {
        val wordList = this.split(" ")

        wordList.forEachIndexed { index, word ->
            val textView = LayoutInflater.from(requireContext()).inflate(resource, null) as TextView

            textView.text =
                if (index == wordList.lastIndex && word != " ") word else "$word "

            binding.layoutQuestionContent.addView(textView)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
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