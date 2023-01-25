package com.clonect.feeltalk.presentation.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.domain.model.user.Emotion
import com.clonect.feeltalk.presentation.util.addTextGradient
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

        collectState()

        binding.apply {

            textDDayValue.text = "32"
            textDDayValue.addTextGradient()
            textDDayUnit.addTextGradient()

            textMyName.text = "jenny"
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
        }
    }


    private fun collectState() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.myEmotionState.collectLatest {
                    binding.ivMyEmotion.setEmotion(it)
                }
            }

            launch {
                viewModel.partnerEmotionState.collectLatest {
                    binding.ivPartnerEmotion.setEmotion(it)
                }
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

        Glide.with(requireContext())
            .load(emotionId)
            .into(this)
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