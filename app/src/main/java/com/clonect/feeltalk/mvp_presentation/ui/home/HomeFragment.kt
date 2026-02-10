package com.clonect.feeltalk.mvp_presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.mvp_presentation.ui.bottom_navigation.BottomNavigationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val navViewModel: BottomNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        restoreScrollViewState()
        return binding.root
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        collectTodayQuestion()
//        collectCoupleAnniversary()
//        collectUserInfo()
//        collectPartnerInfo()
//        collectPartnerClickCount()
//        collectToast()
//        collectAppSettings()
//
//        binding.apply {
//
//            textDDayValue.addTextGradient()
//            textDDayUnit.addTextGradient()
//
//            btnTodayQuestion.setOnClickListener {
//                navigateByQuestion()
//            }
//            layoutTodayQuestionTitle.setOnClickListener {
//                navigateByQuestion()
//            }
//            btnNews.setOnClickListener {
//                navigateToNewsPage()
//            }
//
//            cvMyEmotion.setOnClickListener {
//                showMyEmotionChanger()
//            }
//            cvPartnerEmotion.setOnClickListener {
//                viewModel.increaseClickCount()
//            }
//        }
//    }
//
//    private fun collectAppSettings() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.appSettings.collectLatest {
//                if (it.isNotificationUpdated) {
//                    binding.btnNews.setImageResource(R.drawable.ic_notification_unread)
//                } else {
//                    binding.btnNews.setImageResource(R.drawable.ic_notification)
//                }
//            }
//        }
//    }
//
//    private fun collectToast() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.toast.collectLatest {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun collectPartnerClickCount() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.partnerClickCount.collectLatest {
//                if (it >= 5) {
//                    viewModel.clearClickCount()
//                    viewModel.requestChangingPartnerEmotion()
//                }
//            }
//        }
//    }
//
//    private fun collectTodayQuestion() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.todayQuestion.collectLatest {
//                setQuestionLetterText(it)
//            }
//        }
//    }
//
//    private fun collectCoupleAnniversary() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.dday.collectLatest { dday ->
//                dday?.let {
//                    binding.textDDayValue.text = dday.toString()
//                }
//            }
//        }
//    }
//
//    private fun collectUserInfo() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.userInfo.collectLatest {
//                binding.textMyName.text = it.nickname
//                binding.ivMyEmotion.setEmotion(it.emotion)
//                binding.cvMyEmotion.setEmotionBackground(it.emotion)
//            }
//        }
//    }
//
//    private fun collectPartnerInfo() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.partnerInfo.collectLatest {
//                binding.textPartnerName.text = it.nickname
//                binding.ivPartnerEmotion.setEmotion(it.emotion)
//                binding.cvPartnerEmotion.setEmotionBackground(it.emotion)
//            }
//        }
//    }
//
//
//    private fun ImageView.setEmotion(emotion: Emotion) {
//        val emotionId = when (emotion) {
//            is Emotion.Happy -> R.drawable.ic_emotion_happy
//            is Emotion.Puzzling -> R.drawable.ic_emotion_puzzling
//            is Emotion.Bad -> R.drawable.ic_emotion_bad
//            is Emotion.Angry -> R.drawable.ic_emotion_angry
//        }
//        setImageResource(emotionId)
//    }
//
//    private fun CardView.setEmotionBackground(emotion: Emotion) {
//        val backgroundColor = when (emotion) {
//            is Emotion.Happy -> R.color.emotion_happy_color
//            is Emotion.Puzzling -> R.color.emotion_puzzling_color
//            is Emotion.Bad -> R.color.emotion_bad_color
//            is Emotion.Angry -> R.color.emotion_angry_color
//        }
//        setCardBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColor))
//    }
//
//    private fun showMyEmotionChanger() {
//        val currentEmotion = viewModel.userInfo.value.emotion
//        showMyEmotionChangerDialog(currentEmotion) {
//            viewModel.changeMyEmotion(it)
//        }
//    }
//
//    private fun setQuestionLetterText(question: Question?) = binding.apply {
//        if (question?.question == null) {
//            llLetterContent.visibility = View.GONE
//            llLetterContent.animate()
//                .alpha(0f)
//                .setDuration(0)
//                .start()
//            return@apply
//        }
//
//        if (question.question.isBlank()) {
//            textLetterTitle.text = "질문이 준비되지 않았습니다"
//            textLetterMessage.text = "조금만 기다려주세요"
//            llLetterContent.animate()
//                .alpha(1f)
//                .setDuration(100)
//                .withStartAction {
//                    llLetterContent.visibility = View.VISIBLE
//                }.start()
//            return@apply
//        }
//        if (question.myAnswer == null) {
//            textLetterTitle.text = getString(R.string.letter_paper_title_empty)
//            textLetterMessage.text = getString(R.string.letter_paper_message)
//            llLetterContent.animate()
//                .alpha(1f)
//                .setDuration(100)
//                .withStartAction {
//                    llLetterContent.visibility = View.VISIBLE
//                }.start()
//            return@apply
//        }
//        if (question.partnerAnswer == null) {
//            textLetterTitle.text = "내가 답변한 질문 !"
//            textLetterMessage.text = "내 답변 확인하러 가기"
//            llLetterContent.animate()
//                .alpha(1f)
//                .setDuration(100)
//                .withStartAction {
//                    llLetterContent.visibility = View.VISIBLE
//                }.start()
//            return@apply
//        }
//
//        textLetterTitle.text = getString(R.string.letter_paper_title_partner_written)
//        textLetterMessage.text = getString(R.string.letter_paper_message)
//        llLetterContent.animate()
//            .alpha(1f)
//            .setDuration(100)
//            .withStartAction {
//                llLetterContent.visibility = View.VISIBLE
//            }.start()
//    }
//
//    private fun restoreScrollViewState() {
//        binding.root.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
//            override fun onPreDraw(): Boolean {
//                binding.scrollView.viewTreeObserver.removeOnPreDrawListener(this)
//                navViewModel.homeScrollState.value?.let {
//                    binding.scrollView.scrollY = it
//                    navViewModel.setHomeScrollState(null)
//                }
//                return false
//            }
//        })
//    }
//
//    private fun navigateByQuestion() {
//        val question = viewModel.todayQuestion.value ?: return
//        if (question.question.isBlank()) return
//
//        if (question.myAnswer == null) {
//            navigateToTodayQuestionPage()
//            return
//        }
//
//        navigateToChatPage()
//    }
//
//
//    private fun navigateToNewsPage() {
//        requireParentFragment()
//            .requireParentFragment()
//            .findNavController()
//            .navigate(R.id.action_bottomNavigationFragment_to_newsFragment)
//    }
//
//
//    private fun navigateToTodayQuestionPage() {
//        val bundle = bundleOf(
//            "selectedQuestion" to viewModel.todayQuestion.value
//        )
//        requireParentFragment()
//            .requireParentFragment()
//            .findNavController()
//            .navigate(R.id.action_bottomNavigationFragment_to_todayQuestionFragment, bundle)
//    }
//
//    private fun navigateToChatPage() {
//        val bundle = bundleOf(
//            "selectedQuestion" to viewModel.todayQuestion.value?.copy()
//        )
//        requireParentFragment()
//            .requireParentFragment()
//            .findNavController()
//            .navigate(R.id.action_bottomNavigationFragment_to_chatFragment, bundle)
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        viewModel.getAppSettings()
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        onBackCallback = object: OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                this@HomeFragment.requireActivity().finish()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
//
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        onBackCallback.remove()
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        val scrollState = binding.scrollView.scrollY
//        navViewModel.setHomeScrollState(scrollState)
//    }
}