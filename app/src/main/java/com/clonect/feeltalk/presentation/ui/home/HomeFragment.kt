package com.clonect.feeltalk.presentation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

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

        setLetterPaperSize()

        return binding.root
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

    private fun makeHomeContentVisible(enabled: Boolean) {
        if (enabled) {
            binding.llContentWrapper.visibility = View.VISIBLE
            return
        }
        binding.llContentWrapper.visibility = View.GONE
    }

    private fun setLetterPaperSize() {
//        makeHomeContentVisible(false)

        binding.run {
            llLetterContent.post {
                val contentHeight = llLetterContent.height
                val params = llLetterContent.layoutParams as FrameLayout.LayoutParams
                val contentMargin = params.bottomMargin + params.topMargin
                val totalHeight = contentHeight + contentMargin * 2 + (contentMargin * 2 / 4)
                val letterBottomMargin = (-1 * totalHeight / 3.7).toInt()

                (ivLetterPaper.layoutParams as FrameLayout.LayoutParams).apply {
                    setMargins(0, 0, 0, letterBottomMargin)
                    height = totalHeight
                }.also {
                    ivLetterPaper.layoutParams = it
                }

                setTopSpacerMargin()
            }
        }
    }

    private fun setTopSpacerMargin() {
        binding.run {
            ivLetterPaper.post {
                val bottomSpacerHeight = spacerBottom.height.toFloat()
                val bottomSpacerDp = pxToDp(bottomSpacerHeight)
                val topSpacerDp = if (bottomSpacerDp >= 30) 30f else bottomSpacerDp / 2
                val topSpacerPx = dpToPx(topSpacerDp).toInt()

                val params = llContentWrapper.layoutParams as FrameLayout.LayoutParams
                params.setMargins(0, topSpacerPx, 0, 0)
                llContentWrapper.layoutParams = params

//                makeHomeContentVisible(true)
            }
        }
    }
}