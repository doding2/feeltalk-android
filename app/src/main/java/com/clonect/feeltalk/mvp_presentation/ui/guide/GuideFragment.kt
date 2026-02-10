package com.clonect.feeltalk.mvp_presentation.ui.guide

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentGuideBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GuideFragment : Fragment() {

    private lateinit var binding: FragmentGuideBinding
    private lateinit var onBackCallback: OnBackPressedCallback
    @Inject
    lateinit var adapter: GuideAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGuideBinding.inflate(inflater, container, false)
        setStatusBarColor(requireContext().getColor(R.color.guide_background), true)
        setNavigationBarColor(requireContext().getColor(R.color.guide_background), true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        binding.btnNext.setOnClickListener {
            navigateToCoupleRegistrationPage()
        }
    }


    private fun navigateToCoupleRegistrationPage() {
        findNavController().navigate(R.id.action_guideFragment_to_coupleRegistrationFragment)
    }


    private fun initRecyclerView() = binding.apply {
        val guideList = listOf(
            R.drawable.guide_1,
            R.drawable.guide_2,
            R.drawable.guide_3,
        )
        vpGuide.adapter = adapter.apply {
            items = guideList
        }
        vpGuide.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int, ) {}
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == guideList.size - 1) {
                    enableNextButton(true)
                } else {
                    enableNextButton(false)
                }
            }
        })

//        ariIndicator.attachTo(vpGuide)
        enableNextButton(false)
    }


    private fun enableNextButton(enabled: Boolean) = binding.btnNext.apply {
        isEnabled = enabled
        setCardBackgroundColor(
            if (enabled) requireContext().getColor(R.color.guide_indicator_enabled_button_color)
            else requireContext().getColor(R.color.guide_indicator_disabled_button_color)
        )
    }


    private fun setStatusBarColor(
        @ColorInt color: Int,
        isLight: Boolean
    ) {
        activity?.window?.apply {
            statusBarColor = color
            WindowInsetsControllerCompat(this, binding.root).apply {
                isAppearanceLightStatusBars = isLight
            }
        }
    }

    private fun setNavigationBarColor(
        @ColorInt color: Int,
        isLight: Boolean
    ) {
        activity?.window?.apply {
            navigationBarColor = color
            WindowInsetsControllerCompat(this, binding.root).apply {
                isAppearanceLightNavigationBars = isLight
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}