package com.clonect.feeltalk.presentation.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.presentation.utils.addTextGradient
import com.clonect.feeltalk.presentation.utils.showMyEmotionChangerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        collectUserInfo()
        collectPartnerEmotion()

        binding.apply {

            textDDayValue.text = "32"
            textDDayValue.addTextGradient()
            textDDayUnit.addTextGradient()

            textPartnerName.text = "Daniel"

            btnTodayQuestion.setOnClickListener {
                navigateToTodayQuestionPage()
            }
            layoutTodayQuestionTitle.setOnClickListener {
                navigateToTodayQuestionPage()
            }
            btnNews.setOnClickListener {
                navigateToNewsPage()
            }

            cvMyEmotion.setOnClickListener {
                showMyEmotionChanger()
            }

            cvPartnerEmotion.setOnClickListener {
                viewModel.sendNotification()
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

    private fun collectPartnerEmotion() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerEmotionState.collectLatest {
                binding.ivPartnerEmotion.setEmotion(it)
                binding.cvPartnerEmotion.setEmotionBackground(it)
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


    private fun navigateToNewsPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_newsFragment)
    }

    private fun navigateToTodayQuestionPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_todayQuestionFragment)
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