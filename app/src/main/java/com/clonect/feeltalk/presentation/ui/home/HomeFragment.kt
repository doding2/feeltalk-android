package com.clonect.feeltalk.presentation.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.presentation.util.addTextGradient
import com.clonect.feeltalk.presentation.util.dpToPx
import com.clonect.feeltalk.presentation.util.pxToDp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
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

        binding.apply {
            textLogo.addTextGradient()

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