package com.clonect.feeltalk.presentation.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.presentation.utils.addTextGradient
import com.clonect.feeltalk.presentation.utils.showMyEmotionChangerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectTodayQuestion()
        collectCoupleAnniversary()
        collectUserInfo()
        collectPartnerInfo()

        binding.apply {

            textDDayValue.addTextGradient()
            textDDayUnit.addTextGradient()

            btnTodayQuestion.setOnClickListener {
                navigateByQuestion()
            }
            layoutTodayQuestionTitle.setOnClickListener {
                navigateByQuestion()
            }
            btnNews.setOnClickListener {
                navigateToNewsPage()
            }

            cvMyEmotion.setOnClickListener {
                showMyEmotionChanger()
            }

            cvPartnerEmotion.setOnClickListener {
                // TODO 5번 클릭하면 감정 물어봄
            }
        }
    }


    private fun collectTodayQuestion() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.todayQuestion.collectLatest {
                setQuestionLetterText(it)
            }
        }
    }

    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.dday.collectLatest { dday ->
                dday?.let {
                    binding.textDDayValue.text = dday.toString()
                }
            }
        }
    }

    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
                binding.textMyName.text = it.nickname
                binding.ivMyEmotion.setEmotion(it.emotion)
                binding.cvMyEmotion.setEmotionBackground(it.emotion)
            }
        }
    }

    private fun collectPartnerInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerInfo.collectLatest {
                binding.textPartnerName.text = it.nickname
                binding.ivPartnerEmotion.setEmotion(it.emotion)
                binding.cvPartnerEmotion.setEmotionBackground(it.emotion)
            }
        }
    }

    private fun ImageView.setEmotion(emotion: Emotion) {
        val emotionId = when (emotion) {
            is Emotion.Happy -> R.drawable.ic_emotion_happy
            is Emotion.Puzzling -> R.drawable.ic_emotion_puzzling
            is Emotion.Bad -> R.drawable.ic_emotion_bad
            is Emotion.Angry -> R.drawable.ic_emotion_angry
        }
        setImageResource(emotionId)
    }

    private fun CardView.setEmotionBackground(emotion: Emotion) {
        val backgroundColor = when (emotion) {
            is Emotion.Happy -> R.color.emotion_happy_color
            is Emotion.Puzzling -> R.color.emotion_puzzling_color
            is Emotion.Bad -> R.color.emotion_bad_color
            is Emotion.Angry -> R.color.emotion_angry_color
        }
        setCardBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColor))
    }

    private fun showMyEmotionChanger() {
        val currentEmotion = viewModel.userInfo.value.emotion
        showMyEmotionChangerDialog(currentEmotion) {
            viewModel.changeMyEmotion(it)
        }
    }

    private fun setQuestionLetterText(question: Question?) = binding.apply {
        if (question == null) {
            textLetterTitle.visibility = View.GONE
            textLetterMessage.visibility = View.GONE
        } else {
            textLetterTitle.visibility = View.VISIBLE
            textLetterMessage.visibility = View.VISIBLE
        }

        if (question?.question == "") {
            textLetterTitle.text = "질문이 준비되지 않았습니다."
            textLetterMessage.text = "조금만 기다려주세요"
            return@apply
        }
        if (question?.myAnswer == null) {
            textLetterTitle.text = getString(R.string.letter_paper_title_empty)
            textLetterMessage.text = getString(R.string.letter_paper_message)
            return@apply
        }
        if (question.partnerAnswer == null) {
            textLetterTitle.text = "내가 답변한 질문 !"
            textLetterMessage.text = "내 답변 확인하러 가기"
            return@apply
        }

        textLetterTitle.text = getString(R.string.letter_paper_title_partner_written)
        textLetterMessage.text = getString(R.string.letter_paper_message)
    }


    private fun navigateByQuestion() {
        val question = viewModel.todayQuestion.value ?: return

        if (question.myAnswer == null) {
            navigateToTodayQuestionPage()
            return
        }

        navigateToChatPage()
    }


    private fun navigateToNewsPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_newsFragment)
    }


    private fun navigateToTodayQuestionPage() {
        val bundle = bundleOf(
            "selectedQuestion" to viewModel.todayQuestion.value
        )
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_todayQuestionFragment, bundle)
    }

    private fun navigateToChatPage() {
        val bundle = bundleOf(
            "selectedQuestion" to viewModel.todayQuestion.value?.copy()
        )
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_chatFragment, bundle)
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@HomeFragment.requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)

    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}