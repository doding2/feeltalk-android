package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentContentsShareBinding
import com.clonect.feeltalk.databinding.TabItemContentsShareBinding
import com.clonect.feeltalk.new_presentation.ui.util.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContentsShareFragment : Fragment() {

    private lateinit var binding: FragmentContentsShareBinding
    private val viewModel: ContentsShareViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentContentsShareBinding.inflate(inflater, container, false)

        val fromBubble = arguments?.getBoolean("fromBubble", false) ?: false
        if (fromBubble) {
            binding.root.setPadding(0, 0, 0, requireContext().dpToPx(20f).toInt())
            return binding.root
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            setLightStatusBars(true, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, Color.WHITE, true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { findNavController().popBackStack() }
            mcvShare.setOnClickListener { shareContents() }
        }
    }

    private fun shareContents() = binding.run {
        val selectedTab = tbContentsTabs.selectedTabPosition
        if (selectedTab == 0) {
            viewModel.shareQuestion {
                navigateBack()
            }
        } else {
            viewModel.shareChallenge {
                navigateBack()
            }
        }
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }


    private fun initTabLayout() = binding.run {
        tbContentsTabs.addTab(tbContentsTabs.newTab())
        tbContentsTabs.addTab(tbContentsTabs.newTab())
        vp2Navigation.adapter = ContentsShareAdapter(childFragmentManager, lifecycle)

        tbContentsTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> tab.setTabLayout(isQuestion = true, enabled = true)
                    1 -> tab.setTabLayout(isQuestion = false, enabled = true)
                }
                calculateEnableShare()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> tab.setTabLayout(isQuestion = true, enabled = false)
                    1 -> tab.setTabLayout(isQuestion = false, enabled = false)
                }
            }
        })

        TabLayoutMediator(tbContentsTabs, vp2Navigation) { tab, position ->
            when (position) {
                0 -> {
                    tab.setCustomView(R.layout.tab_item_contents_share)
                    tab.setTabLayout(isQuestion = true, enabled = tab.isSelected)
                }
                1 -> {
                    tab.setCustomView(R.layout.tab_item_contents_share)
                    tab.setTabLayout(isQuestion = false, enabled = tab.isSelected)
                }
            }
        }.attach()
    }

    private fun TabLayout.Tab.setTabLayout(isQuestion: Boolean, enabled: Boolean) {
        val tabBinding = TabItemContentsShareBinding.bind(customView ?: return)
        tabBinding.run {
            if (isQuestion) {
                tvBody.setText(R.string.contents_share_tab_question)
            } else {
                tvBody.setText(R.string.contents_share_tab_challenge)
            }

            val dividerParams = divider.layoutParams as LinearLayout.LayoutParams

            if (enabled) {
                tvBody.setTextColor(requireContext().getColor(R.color.black))
                divider.setBackgroundColor(requireContext().getColor(R.color.black))
                dividerParams.height = activity.dpToPx(2f).toInt()
                divider.layoutParams = dividerParams
            } else {
                tvBody.setTextColor(requireContext().getColor(R.color.gray_400))
                divider.setBackgroundColor(requireContext().getColor(R.color.gray_400))
                dividerParams.height = activity.dpToPx(1f).toInt()
                divider.layoutParams = dividerParams
            }
        }
    }


    private fun calculateEnableShare() = binding.run {
        val selectedTab = tbContentsTabs.selectedTabPosition
        if (selectedTab == 0) {
            enableShareButton(viewModel.getShareQuestionEnabled())
        } else {
            enableShareButton(viewModel.getShareChallengeEnabled())
        }
    }

    private fun enableShareButton(enabled: Boolean) = binding.mcvShare.run {
        if (enabled) {
            setCardBackgroundColor(resources.getColor(R.color.main_500, null))
            isEnabled = true
        } else {
            setCardBackgroundColor(resources.getColor(R.color.main_400, null))
            isEnabled = false
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.selectedQuestion.collectLatest {
                    calculateEnableShare()
                }
            }
            launch {
                viewModel.selectedChallenge.collectLatest {
                    calculateEnableShare()
                }
            }
        }
    }
}